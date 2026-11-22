#!/usr/bin/env python3
"""Genera un tablero HTML seguro con cobertura, timing y métricas de Prometheus."""

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


def texto(valor, sufijo=""):
    return "sin datos" if valor is None else f"{valor}{sufijo}"


def fila(nombre, valor, detalle):
    return (
        f"<tr><th>{html.escape(nombre)}</th>"
        f"<td>{html.escape(valor)}</td><td>{html.escape(detalle)}</td></tr>"
    )


def main():
    args = argumento()
    cobertura = cobertura_lineas(args.jacoco)
    duracion = None
    if args.deploy_start_ms is not None and args.deploy_end_ms is not None:
        duracion = max(0, args.deploy_end_ms - args.deploy_start_ms) / 1000

    cpu = consulta_prometheus(args.prometheus_url, 'avg(process_cpu_usage{job="ms-auth"}) * 100')
    heap = consulta_prometheus(args.prometheus_url, 'sum(jvm_memory_used_bytes{job="ms-auth",area="heap"}) / 1024 / 1024')
    errores_4xx = consulta_prometheus(
        args.prometheus_url,
        'sum(rate(http_server_requests_seconds_count{job="ms-auth",status=~"4.."}[5m]))',
    )
    errores_5xx = consulta_prometheus(
        args.prometheus_url,
        'sum(rate(http_server_requests_seconds_count{job="ms-auth",status=~"5.."}[5m]))',
    )

    cobertura_texto = "sin datos"
    if cobertura is not None:
        cobertura_texto = f"{cobertura[2]:.2f}% ({cobertura[0]} cubiertas / {cobertura[1]} no cubiertas)"

    sha = html.escape(args.sha)
    run_url = html.escape(args.run_url, quote=True)
    generated = html.escape(time.strftime("%Y-%m-%d %H:%M:%S UTC", time.gmtime()))
    rows = "\n".join(
        [
            fila("Cobertura de líneas JaCoCo", cobertura_texto, "counter LINE de jacoco.xml"),
            fila("Duración kind hasta rollout + salud", texto(duracion, " s"), "sin creación de clúster ni compilación"),
            fila("CPU del proceso Java", texto(None if cpu is None else round(cpu, 2), " %"), "promedio de process_cpu_usage; no es CPU total del pod"),
            fila("Memoria heap JVM", texto(None if heap is None else round(heap, 2), " MiB"), "jvm_memory_used_bytes; no es memoria total del pod"),
            fila("Respuestas HTTP 4xx", texto(None if errores_4xx is None else round(errores_4xx, 4), " /s"), "rate de respuestas 4xx; separado de 5xx"),
            fila(f"{args.http_rejection_code} de prueba observados" if args.http_rejection_code in (401, 403) else "Rechazo de prueba observado", texto(args.http_rejection_count, " respuestas"), "incremento del contador del código rechazado tras cinco peticiones sin autenticación"),
            fila("Respuestas HTTP 5xx", texto(None if errores_5xx is None else round(errores_5xx, 4), " /s"), "rate de respuestas 5xx; errores del servidor"),
        ]
    )
    document = f"""<!doctype html>
<html lang="es"><head><meta charset="utf-8"><meta name="viewport" content="width=device-width,initial-scale=1">
<title>IE3 · ms-auth · {sha}</title>
<style>
body{{font-family:system-ui,sans-serif;background:#f5f7fb;color:#172033;margin:0;padding:2rem}}
main{{max-width:980px;margin:auto}}h1{{margin-bottom:.25rem}}.meta{{color:#526078}}
table{{width:100%;border-collapse:collapse;background:white;border-radius:12px;overflow:hidden;box-shadow:0 4px 20px #17203312}}
th,td{{padding:1rem;text-align:left;border-bottom:1px solid #e7ebf2}}th{{width:32%}}tr:last-child th,tr:last-child td{{border-bottom:0}}
a{{color:#1259c3}}
</style></head><body><main>
<h1>IE3 · tablero de ms-auth</h1>
<p class="meta">Entorno: <strong>{html.escape(args.environment)}</strong> · SHA: <code>{sha}</code> · Generado: {generated}</p>
<p class="meta">Ejecución: <a href="{run_url}">{run_url}</a></p>
<table><thead><tr><th>Métrica</th><th>Valor</th><th>Definición</th></tr></thead><tbody>{rows}</tbody></table>
</main></body></html>
"""
    args.output.write_text(document, encoding="utf-8")


if __name__ == "__main__":
    main()
