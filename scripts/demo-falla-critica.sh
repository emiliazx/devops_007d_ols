#!/usr/bin/env bash
# Demuestra IE6: una dependencia con vulnerabilidad CRITICA (Log4Shell) detiene el pipeline.
# Crea una rama de demo, la sube y debes abrir un PR hacia develop para ver los checks en rojo.
# Para limpiar:  git checkout develop && git branch -D demo/falla-critica-ep3 && git push origin --delete demo/falla-critica-ep3
set -euo pipefail
cd "$(dirname "$0")/.."
BRANCH="demo/falla-critica-ep3"

git checkout -b "$BRANCH"
python3 - <<'PY'
p = "pom.xml"
s = open(p, encoding="utf-8").read()
dep = """
        <!-- DEMO EP3: dependencia vulnerable (CVE-2021-44228). NO FUSIONAR. -->
        <dependency>
            <groupId>org.apache.logging.log4j</groupId>
            <artifactId>log4j-core</artifactId>
            <version>2.14.1</version>
        </dependency>
"""
i = s.rfind("</dependencies>")
open(p, "w", encoding="utf-8").write(s[:i] + dep.lstrip("\n") + "    " + s[i:])
PY
git add pom.xml
git commit -m "test(demo): agrega dependencia critica para demostrar bloqueo del pipeline"
git push -u origin "$BRANCH"
echo
echo "Abre un Pull Request de $BRANCH hacia develop."
echo "Resultado esperado: 'Security scan' (Snyk) y 'Container image scan' (Trivy) fallan,"
echo "'Kubernetes deploy (kind)' y 'Deploy smoke test' no se ejecutan / quedan bloqueados."
