#!/usr/bin/env python3
"""Genera un dashboard HTML autocontenido con cobertura, timing y Prometheus."""

import argparse
import html
import json
import time
import urllib.parse
import urllib.request
import xml.etree.ElementTree as ET
from pathlib import Path


def argumento():
    parser = argparse.ArgumentParser()
    parser.add_argument("--jacoco", type=Path, required=True)
    parser.add_argument("--prometheus-url", required=True)
    parser.add_argument("--output", type=Path, required=True)
    parser.add_argument("--sha", required=True)
    parser.add_argument("--run-url", required=True)
    parser.add_argument("--environment", default="kind")
    parser.add_argument("--deploy-start-ms", type=int)
    parser.add_argument("--deploy-end-ms", type=int)
    parser.add_argument("--http-rejection-code", type=int)
    parser.add_argument("--http-rejection-count", type=int)
    return parser.parse_args()


def cobertura_lineas(ruta):
    if not ruta.is_file():
        return None
    root = ET.parse(ruta).getroot()
    for counter in root.findall("./counter"):
        if counter.get("type") == "LINE":
            missed = int(counter.get("missed", "0"))
            covered = int(counter.get("covered", "0"))
            total = missed + covered
            return (covered, missed, covered * 100 / total) if total else None
    return None


def consulta_prometheus(base_url, query):
    url = f"{base_url.rstrip('/')}/api/v1/query?{urllib.parse.urlencode({'query': query})}"
    try:
        with urllib.request.urlopen(url, timeout=10) as response:
            payload = json.load(response)
        resultados = payload.get("data", {}).get("result", [])
        valores = []
        for resultado in resultados:
            valor = resultado.get("value", [None, None])[1]
            try:
                numero = float(valor)
                if numero == numero and numero not in (float("inf"), float("-inf")):
                    valores.append(numero)
            except (TypeError, ValueError):
                continue
        return sum(valores) if valores else None
    except (OSError, ValueError, KeyError, json.JSONDecodeError):
        return None


def escape(valor):
    return html.escape(str(valor), quote=True)


def texto(valor, sufijo=""):
    return "Sin datos" if valor is None else f"{valor}{sufijo}"


def icono(nombre):
    paths = {
        "coverage": '<path d="M4 19h16M6 16V9m4 7V5m4 11v-3m4 3V7"/>',
        "time": '<circle cx="12" cy="12" r="8"/><path d="M12 7v5l3 2"/>',
        "cpu": '<rect x="7" y="7" width="10" height="10" rx="1"/><path d="M9 1v4m6-4v4M9 19v4m6-4v4M1 9h4m-4 6h4m10-6h4m-4 6h4"/>',
        "memory": '<rect x="4" y="6" width="16" height="12" rx="2"/><path d="M8 10h2m4 0h2M8 14h8M8 2v4m8-4v4m-8 12v4m8-4v4"/>',
        "http": '<path d="M4 5h16v14H4z"/><path d="M4 9h16M8 3v4m8-4v4"/>',
        "shield": '<path d="M12 3l7 3v5c0 4-3 7-7 10-4-3-7-6-7-10V6l7-3z"/><path d="M9 12l2 2 4-4"/>',
    }
    return f'<svg class="icon" viewBox="0 0 24 24" aria-hidden="true">{paths[nombre]}</svg>'


def kpi(icon_name, eyebrow, title, value, detail, tone="blue"):
    return f"""<article class="kpi kpi-{tone}">
      <div class="kpi-top">{icono(icon_name)}<span>{escape(eyebrow)}</span></div>
      <h3>{escape(title)}</h3>
      <div class="kpi-value">{escape(value)}</div>
      <p>{escape(detail)}</p>
    </article>"""


def generar_documento(args, cobertura, duracion, cpu, heap, errores_4xx, errores_5xx):
    sha = escape(args.sha)
    environment = escape(args.environment)
    run_url = escape(args.run_url)
    generated = escape(time.strftime("%Y-%m-%d %H:%M:%S UTC", time.gmtime()))

    if cobertura is None:
        coverage_value = "Sin datos"
        coverage_detail = "No se encontró un contador LINE en jacoco.xml"
        coverage_visual = '<div class="coverage-ring no-data"><span>Sin<br>datos</span></div>'
    else:
        coverage_value = f"{cobertura[2]:.2f}%"
        coverage_detail = f"{cobertura[0]} cubiertas · {cobertura[1]} no cubiertas"
        coverage_visual = f'<div class="coverage-ring" style="--coverage:{cobertura[2]:.2f}%"><span>{escape(coverage_value)}</span></div>'

    rejection_title = (
        f"HTTP {args.http_rejection_code} de prueba"
        if args.http_rejection_code in (401, 403)
        else "Rechazo HTTP de prueba"
    )
    rejection_value = texto(args.http_rejection_count, " respuestas")
    rejection_detail = "incremento observado del contador; no es un error 5xx"
    cards = "\n".join(
        [
            kpi("time", "DEPLOYMENT", "Tiempo kind", texto(duracion, " s"), "rollout + health=UP · sin cluster ni compilación", "cyan"),
            kpi("cpu", "JAVA PROCESS", "CPU del proceso", texto(None if cpu is None else round(cpu, 2), " %"), "process_cpu_usage · no es CPU total del pod", "blue"),
            kpi("memory", "JVM HEAP", "Memoria heap", texto(None if heap is None else round(heap, 2), " MiB"), "jvm_memory_used_bytes · no es memoria total del pod", "violet"),
        ]
    )
    document = f"""<!doctype html>
<html lang="es">
<head>
  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width,initial-scale=1">
  <title>MS-AUTH Dashboard · {sha}</title>
  <style>
    :root {{ color-scheme: dark; --bg:#07111f; --panel:#0d1b2d; --panel-2:#10233a; --line:#1d3854; --text:#e8f1fb; --muted:#8da5bd; --cyan:#39d8ff; --blue:#5b8cff; --violet:#a77bff; --green:#53e39b; --amber:#ffca6b; --red:#ff7186; }}
    * {{ box-sizing:border-box; }} body {{ margin:0; min-height:100vh; background:radial-gradient(circle at 85% -10%,#123152 0,transparent 38%),var(--bg); color:var(--text); font:15px/1.5 Inter,ui-sans-serif,system-ui,-apple-system,sans-serif; }}
    main {{ max-width:1220px; margin:0 auto; padding:30px 22px 42px; }} .header {{ display:flex; justify-content:space-between; gap:24px; align-items:flex-start; margin-bottom:28px; }}
    .eyebrow,.kpi-top span {{ color:var(--muted); font-size:11px; font-weight:800; letter-spacing:.14em; text-transform:uppercase; }} h1 {{ margin:5px 0 8px; font-size:clamp(28px,5vw,46px); letter-spacing:-.04em; }} h2 {{ margin:0; font-size:19px; }} h3 {{ margin:16px 0 5px; font-size:14px; color:#c9d8e8; }} p {{ margin:0; color:var(--muted); }} .subtitle {{ max-width:680px; }}
    .status {{ display:inline-flex; align-items:center; gap:8px; border:1px solid #245d61; border-radius:999px; padding:8px 13px; color:#9af3d0; background:#10333a; font-size:12px; font-weight:750; white-space:nowrap; }} .status::before {{ content:""; width:7px; height:7px; border-radius:50%; background:var(--green); box-shadow:0 0 12px var(--green); }}
    .meta {{ display:flex; flex-wrap:wrap; gap:8px 18px; margin-top:18px; color:var(--muted); font-size:12px; }} code {{ color:#b9d8ff; background:#09182a; border:1px solid var(--line); border-radius:5px; padding:2px 5px; }} a {{ color:var(--cyan); }} .section {{ margin-top:30px; }} .section-heading {{ display:flex; justify-content:space-between; align-items:baseline; gap:16px; margin-bottom:13px; }} .section-heading p {{ font-size:12px; }}
    .grid {{ display:grid; grid-template-columns:repeat(3,minmax(0,1fr)); gap:14px; }} .kpi,.panel {{ background:linear-gradient(145deg,#10233a,#0b192b); border:1px solid var(--line); border-radius:14px; box-shadow:0 14px 34px #00000024; }} .kpi {{ min-height:173px; padding:18px; position:relative; overflow:hidden; }} .kpi::after {{ content:""; position:absolute; width:150px; height:150px; right:-80px; bottom:-90px; border-radius:50%; background:var(--accent); opacity:.12; }} .kpi-blue {{ --accent:var(--blue); }} .kpi-cyan {{ --accent:var(--cyan); }} .kpi-violet {{ --accent:var(--violet); }} .kpi-top {{ display:flex; align-items:center; gap:9px; }} .icon {{ width:20px; height:20px; fill:none; stroke:var(--accent,var(--cyan)); stroke-linecap:round; stroke-linejoin:round; stroke-width:1.7; }} .kpi-value {{ margin-top:13px; color:#fff; font-size:clamp(24px,3vw,34px); font-weight:800; letter-spacing:-.04em; }} .kpi p {{ font-size:12px; }}
    .coverage-panel {{ display:grid; grid-template-columns:160px 1fr; align-items:center; gap:22px; padding:20px; }} .coverage-ring {{ width:142px; height:142px; display:grid; place-items:center; border-radius:50%; background:conic-gradient(var(--cyan) var(--coverage),#18344d 0); position:relative; }} .coverage-ring::after {{ content:""; position:absolute; inset:11px; border-radius:50%; background:var(--panel-2); }} .coverage-ring span {{ position:relative; z-index:1; color:#fff; font-size:21px; font-weight:800; text-align:center; }} .coverage-ring.no-data {{ background:#18344d; }} .coverage-ring.no-data span {{ color:var(--muted); font-size:14px; line-height:1.15; }} .coverage-copy h3 {{ margin-top:0; font-size:17px; color:var(--text); }} .coverage-copy p {{ margin-top:7px; }}
    .http-grid {{ display:grid; grid-template-columns:repeat(3,minmax(0,1fr)); gap:14px; }} .http-card {{ padding:18px; border-top:3px solid var(--accent); }} .http-card h3 {{ margin-top:0; color:var(--text); }} .http-card .value {{ margin:13px 0 3px; font-size:28px; font-weight:800; }} .http-4xx {{ --accent:var(--amber); }} .http-5xx {{ --accent:var(--red); }} .http-auth {{ --accent:var(--green); }}
    .foot-grid {{ display:grid; grid-template-columns:1fr 1fr; gap:14px; }} .panel {{ padding:18px; }} .panel h3 {{ margin-top:0; color:var(--text); }} .panel ul {{ margin:9px 0 0; padding-left:18px; color:var(--muted); }} .panel li+li {{ margin-top:6px; }} .source {{ font-size:12px; }}
    @media (max-width:760px) {{ .header {{ display:block; }} .status {{ margin-top:18px; }} .grid,.http-grid,.foot-grid {{ grid-template-columns:1fr; }} .coverage-panel {{ grid-template-columns:1fr; justify-items:center; text-align:center; }} }}
  </style>
</head>
<body>
  <main>
    <header class="header"><div><div class="eyebrow">IE3 · DevOps observability</div><h1>MS-AUTH Dashboard</h1><p class="subtitle">Resumen autocontenido del pipeline: calidad, despliegue temporal y señales observadas en Prometheus.</p><div class="meta"><span>Entorno <strong>{environment}</strong></span><span>Generado <strong>{generated}</strong></span><span>SHA <code>{sha}</code></span></div></div><div class="status">kind · pipeline observado</div></header>
    <section class="section"><div class="section-heading"><h2>Calidad y despliegue</h2><p>Valores tomados del run actual</p></div><div class="panel coverage-panel">{coverage_visual}<div class="coverage-copy"><div class="eyebrow">JaCoCo · cobertura de líneas</div><h3>{escape(coverage_value)}</h3><p>{escape(coverage_detail)}</p><p>Fuente: contador <code>LINE</code> de <code>jacoco.xml</code>.</p></div></div><div class="grid" style="margin-top:14px">{cards}</div></section>
    <section class="section"><div class="section-heading"><h2>Tráfico HTTP</h2><p>Errores separados por clase</p></div><div class="http-grid"><article class="panel http-card http-4xx"><h3>Respuestas HTTP 4xx</h3><div class="value">{escape(texto(None if errores_4xx is None else round(errores_4xx, 4), " /s"))}</div><p>Rate general 4xx. Un 401/403 de prueba es un rechazo esperado, no un fallo 5xx.</p></article><article class="panel http-card http-auth"><h3>{escape(rejection_title)}</h3><div class="value">{escape(rejection_value)}</div><p>{escape(rejection_detail)}</p></article><article class="panel http-card http-5xx"><h3>Respuestas HTTP 5xx</h3><div class="value">{escape(texto(None if errores_5xx is None else round(errores_5xx, 4), " /s"))}</div><p>Rate de errores del servidor. No se provoca artificialmente.</p></article></div></section>
    <section class="section foot-grid"><article class="panel"><h3>{icono("shield")} Fuente y límites</h3><ul><li>JaCoCo: reporte XML generado por Maven.</li><li>Prometheus: CPU del proceso Java, heap JVM y tasas HTTP.</li><li>CPU y memoria no representan el consumo total del pod.</li><li>La ausencia de muestra se presenta como <strong>Sin datos</strong>.</li></ul></article><article class="panel"><h3>{icono("http")} Ejecución del workflow</h3><p class="source">Enlace al run asociado a este tablero:</p><p style="margin-top:9px"><a href="{run_url}">{run_url}</a></p><p class="source" style="margin-top:12px">Entorno temporal Kubernetes <strong>kind</strong>; sin dependencias externas de CDN.</p></article></section>
  </main>
</body>
</html>
"""
    return document


def main():
    args = argumento()
    cobertura = cobertura_lineas(args.jacoco)
    duracion = None
    if args.deploy_start_ms is not None and args.deploy_end_ms is not None:
        duracion = max(0, args.deploy_end_ms - args.deploy_start_ms) / 1000
    cpu = consulta_prometheus(args.prometheus_url, 'avg(process_cpu_usage{job="ms-auth"}) * 100')
    heap = consulta_prometheus(args.prometheus_url, 'sum(jvm_memory_used_bytes{job="ms-auth",area="heap"}) / 1024 / 1024')
    errores_4xx = consulta_prometheus(args.prometheus_url, 'sum(rate(http_server_requests_seconds_count{job="ms-auth",status=~"4.."}[5m]))')
    errores_5xx = consulta_prometheus(args.prometheus_url, 'sum(rate(http_server_requests_seconds_count{job="ms-auth",status=~"5.."}[5m]))')
    args.output.write_text(generar_documento(args, cobertura, duracion, cpu, heap, errores_4xx, errores_5xx), encoding="utf-8")


if __name__ == "__main__":
    main()
