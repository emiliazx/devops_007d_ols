#!/usr/bin/env bash
# Comprueba que los logs de la aplicación y MySQL son consultables sin imprimirlos.
set -euo pipefail

verificar_logs() {
  local namespace="$1" selector="$2" nombre="$3" pods pod phase lines

  kubectl -n "$namespace" wait --for=condition=ready pod -l "$selector" --timeout=120s >/dev/null
  pods="$(kubectl -n "$namespace" get pods -l "$selector" -o name)"
  [ -n "$pods" ] || { echo "No se encontraron pods para $nombre" >&2; return 1; }

  while IFS= read -r pod; do
    [ -n "$pod" ] || continue
    phase="$(kubectl -n "$namespace" get "$pod" -o jsonpath='{.status.phase}')"
    [ "$phase" = "Running" ] || { echo "$nombre/$pod no está en Running: $phase" >&2; return 1; }
    lines="$(kubectl -n "$namespace" logs "$pod" --tail=50 | wc -l)"
    if [ "$lines" -eq 0 ]; then
      echo "ADVERTENCIA logs consultables pero sin líneas observadas: $nombre/${pod#pod/} estado=Running contenido=omitido" >&2
    else
      echo "OK logs observados y consultables: $nombre/${pod#pod/} estado=Running lineas_consultadas=$lines contenido=omitido"
    fi
  done <<< "$pods"
}

verificar_logs ms-auth app=ms-auth ms-auth
verificar_logs ms-auth app=mysql mysql
