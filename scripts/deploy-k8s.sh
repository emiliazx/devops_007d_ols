#!/usr/bin/env bash
# Despliega ms-auth + MySQL + Prometheus en el cluster activo de kubectl
# (kind, minikube, GKE, EKS, AKS...). Uso:
#   ./scripts/deploy-k8s.sh
# Opcional: exportar DB_PASSWORD y JWT_SECRET; si no existen se generan.
set -euo pipefail
cd "$(dirname "$0")/.."

DB_PASSWORD="${DB_PASSWORD:-$(openssl rand -hex 24)}"
JWT_SECRET="${JWT_SECRET:-$(openssl rand -base64 48 | tr -d '\n')}"

echo ">> Namespace ms-auth"
kubectl apply -f k8s/namespace.yml

echo ">> Secret ms-auth-secret (no se escribe a disco)"
kubectl -n ms-auth create secret generic ms-auth-secret \
  --from-literal=DB_PASSWORD="$DB_PASSWORD" \
  --from-literal=JWT_SECRET="$JWT_SECRET" \
  --dry-run=client -o yaml | kubectl apply -f -

echo ">> Aplicando manifiestos de la aplicacion"
kubectl apply -k k8s

echo ">> Aplicando monitoreo (Prometheus)"
kubectl apply -k monitoring

echo ">> Esperando rollouts"
kubectl -n ms-auth rollout status deployment/mysql --timeout=300s
kubectl -n ms-auth rollout status deployment/ms-auth --timeout=300s
kubectl -n monitoring rollout status deployment/prometheus --timeout=240s

kubectl -n ms-auth get pods,svc
kubectl -n monitoring get pods,svc