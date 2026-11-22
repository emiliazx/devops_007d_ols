#!/usr/bin/env python3
"""Auditoria de cumplimiento (EP3). Falla (exit 1) ante cualquier incumplimiento.

Revisa manifiestos Kubernetes, configuracion del pipeline y del repositorio.
Uso: python3 scripts/auditoria_cumplimiento.py   (requiere: pip install pyyaml)
"""
import pathlib
import subprocess
import sys

try:
    import yaml
except ImportError:
    print("Falta PyYAML: pip install pyyaml")
    sys.exit(2)

ROOT = pathlib.Path(__file__).resolve().parent.parent
resultados = []


def check(nombre, ok, detalle=""):
    resultados.append((nombre, ok, detalle))


def manifiestos():
    for carpeta in ("k8s", "monitoring/k8s"):
        for f in sorted((ROOT / carpeta).glob("*.yml")):
            if f.name == "kustomization.yml" or f.name.endswith(".example.yml"):
                continue
            for doc in yaml.safe_load_all(f.read_text(encoding="utf-8")):
                if doc:
                    yield f.relative_to(ROOT), doc


def contenedores(spec):
    return list(spec.get("initContainers", []) or []) + list(spec.get("containers", []) or [])


for ruta, doc in manifiestos():
    kind = doc.get("kind")
    nombre = doc.get("metadata", {}).get("name", "?")

    if kind == "Secret":
        check(f"{ruta}: no hay Secret con valores versionados", False, f"Secret/{nombre}")

    if kind in ("Deployment", "StatefulSet", "DaemonSet"):
        pod = doc["spec"]["template"]["spec"]
        pod_sc = pod.get("securityContext", {}) or {}
        ref = f"{ruta} {kind}/{nombre}"
        for c in contenedores(pod):
            cid = f"{ref}/{c['name']}"
            img = c.get("image", "")
            tag = img.rsplit(":", 1)[1] if ":" in img.split("/")[-1] else ""
            check(f"{cid}: imagen con tag fijo (no latest)", bool(tag) and tag != "latest", img)

            res = c.get("resources", {}) or {}
            check(f"{cid}: requests/limits de CPU y memoria",
                  all(k in (res.get(t) or {}) for t in ("requests", "limits") for k in ("cpu", "memory")))

            sc = c.get("securityContext", {}) or {}
            check(f"{cid}: allowPrivilegeEscalation=false", sc.get("allowPrivilegeEscalation") is False)
            check(f"{cid}: runAsNonRoot", sc.get("runAsNonRoot", pod_sc.get("runAsNonRoot")) is True)
            check(f"{cid}: capabilities drop ALL", "ALL" in (sc.get("capabilities", {}) or {}).get("drop", []))

        for c in pod.get("containers", []) or []:
            cid = f"{ref}/{c['name']}"
            check(f"{cid}: readinessProbe y livenessProbe", "readinessProbe" in c and "livenessProbe" in c)

# --- Pipeline: no debe poder saltarse los controles (IE6) ---
ci = (ROOT / ".github/workflows/ci.yml").read_text(encoding="utf-8")
check("ci.yml: sin 'continue-on-error: true'", "continue-on-error: true" not in ci)
check("ci.yml: sin '|| true' que oculte fallos", "|| true" not in ci)
for job in ("security:", "gitleaks:", "sonarcloud:", "trivy-scan:", "k8s-validate:", "k8s-deploy:", "compliance:"):
    check(f"ci.yml: job obligatorio '{job[:-1]}'", f"\n  {job}" in ci)
check("ci.yml: Snyk bloquea severidad high", "--severity-threshold=high" in ci)
check("ci.yml: Trivy bloquea CRITICAL", "--exit-code 1" in ci and "CRITICAL" in ci)

def bloque_job(nombre, siguiente=None):
    bloque = ci.split(f"\n  {nombre}:", 1)[1]
    if siguiente:
        bloque = bloque.split(f"\n  {siguiente}:", 1)[0]
    return bloque


comunes = ("build-and-test", "security", "gitleaks", "docker-build",
           "compliance", "k8s-validate", "trivy-scan")
kind = bloque_job("k8s-deploy", "deploy-smoke-test")
smoke = bloque_job("deploy-smoke-test", "ci-gate")
gate = bloque_job("ci-gate")
check("ci.yml: kind declara dependencias", "needs: [build-and-test, security, gitleaks, docker-build, compliance, k8s-validate, trivy-scan, sonarcloud]" in kind)
check("ci.yml: kind exige success de controles comunes", all(f"needs.{job}.result == 'success'" in kind for job in comunes))
check("ci.yml: kind exige SonarCloud en main", "needs.sonarcloud.result == 'success'" in kind and "github.ref != 'refs/heads/main'" in kind)
check("ci.yml: smoke exige kind y controles comunes", all(f"needs.{job}.result == 'success'" in smoke for job in comunes + ("k8s-deploy",)))
check("ci.yml: smoke no exige SonarCloud en PR", "github.event_name == 'pull_request'" in smoke and "needs.sonarcloud.result == 'success'" in smoke)
check("ci.yml: gate siempre evalua resultados", "name: CI gate" in gate and "if: ${{ always() }}" in gate and "exit 1" in gate)
check("ci.yml: gate depende de todos los jobs evaluados", all(job in gate.split("needs:", 1)[1].split("\n", 1)[0] for job in comunes + ("sonarcloud", "k8s-deploy", "deploy-smoke-test")))

# --- Repositorio ---
check(".github/CODEOWNERS existe", (ROOT / ".github/CODEOWNERS").is_file())
check("plantilla de Pull Request existe", (ROOT / ".github/pull_request_template.md").is_file())
dockerfile = (ROOT / "Dockerfile").read_text(encoding="utf-8")
check("Dockerfile ejecuta como usuario no root", "USER 10001" in dockerfile)
props = (ROOT / "src/main/resources/application.properties").read_text(encoding="utf-8")
check("Actuator expone prometheus", "prometheus" in props.split("exposure.include=")[1].splitlines()[0])
try:
    tracked = subprocess.run(["git", "ls-files", ".env"], cwd=ROOT, capture_output=True, text=True).stdout.strip()
    check(".env no esta versionado", tracked == "")
except FileNotFoundError:
    pass

reglas = yaml.safe_load((ROOT / "monitoring/prometheus/alert-rules.yml").read_text(encoding="utf-8"))
alertas = [r for g in reglas["groups"] for r in g["rules"]]
check("al menos 5 alertas definidas", len(alertas) >= 5, str(len(alertas)))
check("toda alerta declara severity", all("severity" in r.get("labels", {}) for r in alertas))

# --- Reporte ---
fallos = 0
for nombre, ok, detalle in resultados:
    print(f"[{'PASS' if ok else 'FAIL'}] {nombre}" + (f"  ({detalle})" if detalle and not ok else ""))
    fallos += 0 if ok else 1
print(f"\n{len(resultados) - fallos}/{len(resultados)} controles cumplidos")
sys.exit(1 if fallos else 0)
