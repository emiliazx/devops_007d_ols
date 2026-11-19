## Descripción
<!-- Qué cambia y por qué -->
- Integración de manifiestos de Kubernetes (`k8s/` y `monitoring/`).
- Actualización de scripts de despliegue y observabilidad.

## Checklist de cumplimiento
- [ ] Todos los checks del pipeline están en verde (build, Snyk, Gitleaks, SonarCloud, Trivy, auditoría, K8s)
- [ ] Sin secretos ni credenciales en el código
- [ ] Manifiestos K8s con limits/requests, probes y usuario no root
- [ ] Documentación actualizada (README / docs)