# Evidencias técnicas EP2

**Repositorio:** [emiliazx/devops_007d_ols](https://github.com/emiliazx/devops_007d_ols)
**Base de evaluación:** `EP2_DOY0101_Estudiante.pdf`, cinco indicadores IE1–IE5 de 20 % cada uno.
**Alcance:** despliegue simulado mediante GitHub Actions y Docker Compose; no existe despliegue productivo ni escalado horizontal automático demostrado.

### Cómo verificar las evidencias

1. Abrir la [ejecución integral exitosa #39487872794](https://github.com/emiliazx/devops_007d_ols/actions/runs/39487872794) y revisar sus cinco trabajos: pruebas, seguridad, SonarCloud, construcción Docker y despliegue temporal.
2. Comprobar la integración de los cambios en los [PR #29](https://github.com/emiliazx/devops_007d_ols/pull/29) y [PR #30](https://github.com/emiliazx/devops_007d_ols/pull/30).
3. Abrir la [ejecución de `develop` #39491960155](https://github.com/emiliazx/devops_007d_ols/actions/runs/39491960155). Esa ejecución es anterior a T13 y **no incluía Gitleaks**: confirmó build, Snyk y Docker Build, mientras SonarCloud y deploy quedaron omitidos en `develop`. La validación remota de Gitleaks queda pendiente para la ejecución posterior de T13; el Quality Gate y despliegue se comprueban en PR y `main` debido a la limitación del plan de SonarCloud.
4. Contrastar cada indicador con la tabla y el archivo de implementación correspondiente.

Este documento separa implementación, validación y documentación. No constituye una reflexión personal ni asigna una calificación docente.

| Indicador | Requisito de la rúbrica | Implementación | Prueba de funcionamiento | Evidencia GitHub Actions | PR/commits trazables | Limitaciones conocidas | Estado comprobado |
|---|---|---|---|---|---|---|---|
| IE1 | Contenedores eficientes, imágenes y configuración adecuada | [`Dockerfile`](../Dockerfile), [`.dockerignore`](../.dockerignore), job `docker-build` en [`ci.yml`](../.github/workflows/ci.yml) | `docker build` en CI; Compose ejecuta la imagen en smoke test | [Run 39487872794](https://github.com/emiliazx/devops_007d_ols/actions/runs/39487872794) | [PR #29](https://github.com/emiliazx/devops_007d_ols/pull/29), merge `3687d8a` | No se afirma escalado horizontal automático; el runtime local depende de Docker | Implementado y validado remotamente en el run histórico |
| IE2 | Pruebas automatizadas de casos críticos dentro del pipeline | `src/test/`, Maven Wrapper y JaCoCo en [`pom.xml`](../pom.xml) | `clean verify`: 17 pruebas, 0 fallos, 0 errores, 0 omitidas; cobertura XML | [Run 39487872794](https://github.com/emiliazx/devops_007d_ols/actions/runs/39487872794) | [PR #29](https://github.com/emiliazx/devops_007d_ols/pull/29), commits `02d0937` y `d8a3118` | La ejecución local no sustituye una nueva ejecución remota | Implementado, validado localmente y validado remotamente históricamente |
| IE3 | Escalabilidad, seguridad, parámetros y controles de gobernanza | [`.snyk`](../.snyk), `.github/workflows/ci.yml`, [`.gitleaksignore`](../.gitleaksignore), Dependabot, Gitleaks, SonarCloud Quality Gate y límites Compose | Snyk bloquea hallazgos no exceptuados; Gitleaks analiza historial y bloquea secretos nuevos; tres fingerprints históricos concretos quedan exceptuados; SonarCloud espera el gate | Run PR #33 `39511518892` reprodujo tres hallazgos y bloqueó deploy; validación posterior a `.gitleaksignore` aún pendiente en GitHub Actions | [PR #33](https://github.com/emiliazx/devops_007d_ols/pull/33) | Las excepciones requieren revisión humana y no cubren secretos nuevos; JAXB sigue como riesgo residual hasta 2026-11-20 | Implementado localmente; validación remota T13B pendiente |
| IE4 | Despliegue automático simulado con trazabilidad | `deploy-smoke-test` en [`ci.yml`](../.github/workflows/ci.yml), [`docker-compose.yml`](../docker-compose.yml), README | Compose levanta MySQL/app, espera healthchecks, consulta `/actuator/health` y limpia | [Run 39487872794](https://github.com/emiliazx/devops_007d_ols/actions/runs/39487872794) exitoso; [run 39491960155](https://github.com/emiliazx/devops_007d_ols/actions/runs/39491960155) lo omitió correctamente en `develop` | [PR #29](https://github.com/emiliazx/devops_007d_ols/pull/29) → [PR #30](https://github.com/emiliazx/devops_007d_ols/pull/30) → `develop` | No es producción; push directo a develop omite Sonar/deploy por limitación de SonarCloud | Implementado y validado remotamente en ambos comportamientos |
| IE5 | Orquestación estable y adaptable con Compose o Kubernetes | [`docker-compose.yml`](../docker-compose.yml), healthchecks, `depends_on`, límites CPU/memoria | `docker compose config`; smoke test con MySQL y aplicación saludables | [Run 39487872794](https://github.com/emiliazx/devops_007d_ols/actions/runs/39487872794) | [PR #29](https://github.com/emiliazx/devops_007d_ols/pull/29) | Docker Desktop puede no estar disponible localmente; no se declara autoescalado | Implementado y validado remotamente en el run histórico |

## Riesgos y alcances conocidos

- **Seguridad:** Snyk mantiene el umbral `high`. La política [`.snyk`](../.snyk) registra una excepción temporal exclusivamente para `SNYK-JAVA-ORGGLASSFISHJAXB-20883142`, con vencimiento el **20/11/2026**. Un análisis exitoso con esta política **no significa ausencia total de vulnerabilidades**: JAXB sigue siendo un riesgo residual documentado.
- **SonarCloud:** el Quality Gate se ejecuta en los Pull Requests y en `main`; se omite expresamente en pushes directos a `develop` por la restricción del plan respecto a ramas no principales.
- **Orquestación:** Compose administra Spring Boot y MySQL, el orden de arranque, los healthchecks y límites de recursos. No se afirma autoscaling, alta disponibilidad multi-nodo ni despliegue productivo.
- **Dependabot:** se encuentra configurado para Maven, Docker y GitHub Actions. Los PR de actualización no deben fusionarse automáticamente, especialmente aquellos que cambian Java 21 a versiones mayores.
- **Gobernanza:** la aprobación humana y las reglas obligatorias de protección de ramas se deben verificar en la configuración administrativa de GitHub; no se presentan como activadas sin esa comprobación.

## Trazabilidad

- [PR #29 — integración del pipeline EP2](https://github.com/emiliazx/devops_007d_ols/pull/29)
- [PR #30 — estabilización post-merge](https://github.com/emiliazx/devops_007d_ols/pull/30)
- [Merge PR #29, `3687d8a`](https://github.com/emiliazx/devops_007d_ols/commit/3687d8a)
- [Merge PR #30, `b169b16`](https://github.com/emiliazx/devops_007d_ols/commit/b169b16)

La corrección de T12 queda pendiente de un nuevo run remoto posterior a esta rama; los runs anteriores son evidencia histórica y no prueban cambios futuros.

## Criterios antes de la publicación definitiva

- [ ] Las reflexiones EP2 de ambos integrantes están confirmadas como autoría personal y completas.
- [ ] Ambos integrantes han revisado la declaración final de herramientas de IA, incluida la mención anterior a Gemini.
- [ ] El PR de documentación final hacia `develop` terminó con checks satisfactorios y fue revisado.
- [ ] La evidencia de `develop` refleja el último commit y no solamente ejecuciones anteriores.
- [ ] Se verificó la configuración real de revisión y checks requeridos en `main` y `develop` o se consignó su limitación.
- [ ] Solo después de todo lo anterior se publica `develop` hacia `main` mediante PR.

La calificación final corresponde exclusivamente al docente y no puede garantizarse por una matriz de evidencias.
