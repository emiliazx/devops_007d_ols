#!/usr/bin/env bash
# Verifica salud, metricas y scraping de Prometheus en el cluster activo.
set -euo pipefail

APP_PORT=18080
PROM_PORT=19090
PIDS=()
cleanup() { for p in "${PIDS[@]:-}"; do kill "$p" 2>/dev/null || true; done; }
trap cleanup EXIT

kubectl -n ms-auth port-forward svc/ms-auth ${APP_PORT}:8080 >/dev/null 2>&1 & PIDS+=($!)
kubectl -n monitoring port-forward svc/prometheus ${PROM_PORT}:9090 >/dev/null 2>&1 & PIDS+=($!)

echo ">> Salud de la aplicacion"
health="$(curl -fsS --retry 15 --retry-delay 2 --retry-connrefused http://localhost:${APP_PORT}/actuator/health)"
echo "$health"
grep -q '"status":"UP"' <<< "$health"

echo ">> Generando trafico (incluye 401 para registrar errores)"
for _ in 1 2 3 4 5; do
  curl -s -o /dev/null http://localhost:${APP_PORT}/info || true
done

echo ">> Metricas Prometheus expuestas por la app"
metrics="$(curl -fsS http://localhost:${APP_PORT}/actuator/prometheus)"
for m in jvm_memory_used_bytes process_cpu_usage http_server_requests_seconds_count; do
  grep -q "^${m}" <<< "$metrics" && echo "OK  $m" || { echo "FALTA $m"; exit 1; }
done

echo ">> Prometheus listo"
curl -fsS --retry 20 --retry-delay 3 --retry-connrefused http://localhost:${PROM_PORT}/-/ready >/dev/null

echo ">> Esperando que Prometheus vea ms-auth como UP"
ok=0
for _ in $(seq 1 24); do
  res="$(curl -fsS "http://localhost:${PROM_PORT}/api/v1/query?query=up%7Bjob%3D%22ms-auth%22%7D" || true)"
  if grep -Eq '"value":\[[0-9.]+,"1"\]' <<< "$res"; then ok=1; break; fi
  sleep 5
done
[ "$ok" = 1 ] || { echo "Prometheus no ve ms-auth UP"; echo "$res"; exit 1; }
echo "OK  up{job=\"ms-auth\"} = 1"

echo ">> Reglas de alerta cargadas"
curl -fsS "http://localhost:${PROM_PORT}/api/v1/rules" | grep -q MsAuthCaido
echo "OK  MsAuthCaido"

echo "VERIFICACION COMPLETA"
