#!/usr/bin/env bash
# Activa reglas de proteccion de ramas (main y develop) con GitHub CLI.
# Requiere: gh auth login con permisos de administrador del repositorio.
# Uso: ./scripts/configurar-branch-protection.sh [owner/repo]
set -euo pipefail
REPO="${1:-emiliazx/devops_007d_ols}"

read -r -d '' BODY <<'JSON' || true
{
  "required_status_checks": {
    "strict": true,
    "contexts": [
      "Build and test",
      "Security scan",
      "Gitleaks secret scanning",
      "SonarCloud and Quality Gate",
      "Docker build",
      "Compliance audit",
      "Kubernetes manifests validation",
      "Container image scan",
      "Kubernetes deploy (kind)"
    ]
  },
  "enforce_admins": false,
  "required_pull_request_reviews": {
    "required_approving_review_count": 1,
    "dismiss_stale_reviews": true,
    "require_code_owner_reviews": false
  },
  "restrictions": null,
  "required_linear_history": true,
  "allow_force_pushes": false,
  "allow_deletions": false,
  "required_conversation_resolution": true
}
JSON

for branch in main develop; do
  echo ">> Protegiendo ${REPO}:${branch}"
  gh api -X PUT "repos/${REPO}/branches/${branch}/protection" --input - <<< "$BODY" >/dev/null
done

echo ">> Estado final"
for branch in main develop; do
  gh api "repos/${REPO}/branches/${branch}/protection" \
    --jq '"\(.url)\n  checks: \(.required_status_checks.contexts | length)  reviews: \(.required_pull_request_reviews.required_approving_review_count)  force-push: \(.allow_force_pushes.enabled)"'
done
