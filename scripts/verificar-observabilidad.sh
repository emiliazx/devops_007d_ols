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
if [ -n "${GITHUB_ENV:-}" ]; then
  echo "KIND_DEPLOY_END_MS=$(date +%s%3N)" >> "$GITHUB_ENV"
fi

echo ">> Generando trafico sin autenticacion y comprobando el codigo HTTP"
initial_status="$(curl -sS -o /dev/null -w '%{http_code}' http://localhost:${APP_PORT}/info || true)"
case "$initial_status" in
  401|403)
    rejection_code="$initial_status"
    ;;
  000|"")
    echo "Error de conexion al probar /info sin autenticacion; no se obtuvo un codigo HTTP"
    exit 1
    ;;
  *)
    echo "Codigo HTTP inesperado en /info sin autenticacion: $initial_status; se esperaba 401 o 403"
    exit 1
    ;;
esac
if [ -n "${GITHUB_ENV:-}" ]; then
  echo "HTTP_REJECTION_CODE=$rejection_code" >> "$GITHUB_ENV"
fi
echo ">> Rechazo esperado observado: HTTP $rejection_code"

echo ">> Metricas Prometheus expuestas por la app"
metrics="$(curl -fsS http://localhost:${APP_PORT}/actuator/prometheus)"
for m in jvm_memory_used_bytes process_cpu_usage http_server_requests_seconds_count; do
  grep -q "^${m}" <<< "$metrics" && echo "OK  $m" || { echo "FALTA $m"; exit 1; }
done

echo ">> Prometheus listo"
curl --connect-timeout 5 --max-time 10 -fsS --retry 20 --retry-delay 3 --retry-connrefused http://localhost:${PROM_PORT}/-/ready >/dev/null

prom_query() {
  curl --connect-timeout 5 --max-time 10 -fsS -G "http://localhost:${PROM_PORT}/api/v1/query" --data-urlencode "query=$1"
}

prom_count() {
  python3 -c 'import json, sys; result=json.load(sys.stdin)["data"]["result"]; print(int(float(result[0]["value"][1])))' 2>/dev/null
}

wait_for_rejection_count() {
  local minimum="$1" response="" current=""
  for _ in $(seq 1 24); do
    response="$(prom_query "sum(http_server_requests_seconds_count{job=\"ms-auth\",status=\"$rejection_code\"})" || true)"
    current="$(printf '%s' "$response" | prom_count || true)"
    if [ -n "$current" ] && [ "$current" -ge "$minimum" ]; then
      printf '%s' "$current"
      return 0
    fi
    sleep 5
  done
}

echo ">> Esperando la muestra de referencia HTTP $rejection_code (no equivale a up=1)"
baseline_rejection="$(wait_for_rejection_count 1)"
if [ -n "$baseline_rejection" ]; then
  echo ">> Generando cinco HTTP $rejection_code adicionales"
  for _ in 1 2 3 4 5; do
    status="$(curl -sS -o /dev/null -w '%{http_code}' http://localhost:${APP_PORT}/info || true)"
    if [ "$status" != "$rejection_code" ]; then
      if [ "$status" = 000 ] || [ -z "$status" ]; then
        echo "Error de conexion en peticion adicional a /info sin autenticacion"
      else
        echo "Codigo HTTP inconsistente en /info: se esperaba $rejection_code y se obtuvo $status"
      fi
      exit 1
    fi
  done
  target_rejection=$((baseline_rejection + 5))
  echo ">> Esperando el incremento del contador HTTP $rejection_code"
  observed_rejection="$(wait_for_rejection_count "$target_rejection")"
  if [ -n "$observed_rejection" ] && [ -n "${GITHUB_ENV:-}" ]; then
    echo "HTTP_REJECTION_INCREMENT=$((observed_rejection - baseline_rejection))" >> "$GITHUB_ENV"
    echo "OK  HTTP $rejection_code observados: $((observed_rejection - baseline_rejection))"
  else
    echo "Sin muestra de incremento HTTP $rejection_code dentro del limite"
  fi
else
  echo "Sin muestra HTTP $rejection_code de referencia dentro del limite"
fi

echo ">> Esperando que Prometheus vea ms-auth como UP"
ok=0
for _ in $(seq 1 24); do
  res="$(curl --connect-timeout 5 --max-time 10 -fsS "http://localhost:${PROM_PORT}/api/v1/query?query=up%7Bjob%3D%22ms-auth%22%7D" || true)"
  if grep -Eq '"value":\[[0-9.]+,"1"\]' <<< "$res"; then ok=1; break; fi
  sleep 5
done
[ "$ok" = 1 ] || { echo "Prometheus no ve ms-auth UP"; echo "$res"; exit 1; }
echo "OK  up{job=\"ms-auth\"} = 1"

echo ">> Reglas de alerta cargadas"
curl --connect-timeout 5 --max-time 10 -fsS "http://localhost:${PROM_PORT}/api/v1/rules" | grep -q MsAuthCaido
echo "OK  MsAuthCaido"

echo "VERIFICACION COMPLETA"
