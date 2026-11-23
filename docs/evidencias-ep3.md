# Evidencias técnicas — Evaluación 3

**Repositorio:** [emiliazx/devops_007d_ols](https://github.com/emiliazx/devops_007d_ols)  
**Rama de documentación:** `docs/ep3-evidencias`  
**Alcance:** esta matriz sigue la rúbrica IE1–IE6 de la Evaluación Parcial 3. Relaciona la implementación visible en el repositorio con las evidencias remotas indicadas, pero no sustituye las capturas que la evaluación solicite ni redacta las reflexiones personales de los integrantes.

## Cómo leer las evidencias

- Los enlaces a PR y ejecuciones apuntan a GitHub; deben abrirse para capturar el estado, los jobs y los artefactos visibles en cada ejecución.
- `kind` es un clúster Kubernetes temporal creado dentro de un runner de GitHub Actions. El repositorio no demuestra un despliegue en EKS, AWS, Render ni otra nube.
- El workflow actual conserva once jobs: `build-and-test`, `security`, `gitleaks`, `sonarcloud`, `docker-build`, `compliance`, `k8s-validate`, `trivy-scan`, `k8s-deploy`, `deploy-smoke-test` y `CI gate`.
- SonarCloud se ejecuta únicamente cuando el workflow recibe un push a `main`. No se cuenta un `skipped` como aprobación.

## Matriz IE1–IE6

### IE1 — Monitoreo y observabilidad

**Implementación verificable**

- [`monitoring/`](../monitoring/) define Prometheus, RBAC, servicio, configuración de scraping y reglas de alerta.
- [`monitoring/prometheus/prometheus-k8s.yml`](../monitoring/prometheus/prometheus-k8s.yml) descubre pods con etiqueta `app: ms-auth` en el namespace `ms-auth`, usando `/actuator/prometheus` en el puerto 8080.
- [`k8s/app-deployment.yml`](../k8s/app-deployment.yml) expone las anotaciones de scraping y los probes de liveness/readiness de `ms-auth`. También declara requests/limits de CPU y memoria para la aplicación y el init container.
- [`k8s/mysql-deployment.yml`](../k8s/mysql-deployment.yml) define MySQL con probes, requests/limits y almacenamiento temporal del entorno de prueba.
- [`scripts/verificar-observabilidad.sh`](../scripts/verificar-observabilidad.sh) comprueba `/actuator/health`, las métricas expuestas, el scraping `up{job="ms-auth"}` y las reglas cargadas.
- La misma verificación captura el código de rechazo real de `/info` sin autenticación (`401` o `403`), exige que se repita en cinco peticiones y espera el contador correspondiente en Prometheus.
- Las reglas de [`monitoring/prometheus/alert-rules.yml`](../monitoring/prometheus/alert-rules.yml) cubren disponibilidad, errores 5xx, errores en logs, CPU, heap y latencia.

**Métricas y significado**

| Dato observado | Qué mide | Qué no debe afirmarse |
|---|---|---|
| CPU | `process_cpu_usage`: CPU del proceso Java expuesto por la aplicación | No es el consumo total del pod ni del nodo |
| Memoria | `jvm_memory_used_bytes` con `area="heap"`: heap usado de la JVM | No es la memoria total del pod |
| HTTP 4xx | Tasa general de respuestas 4xx y el contador del código de rechazo de la prueba | Un `401` o `403` de prueba no es un 5xx |
| HTTP 5xx | Tasa de respuestas 5xx | No se provoca artificialmente un 5xx |
| Disponibilidad | `up{job="ms-auth"}` y `/actuator/health` | `up == 1` por sí solo no demuestra que se observaron errores HTTP |

**Validación reproducible**

En el runner Linux del workflow:

```bash
./scripts/verificar-observabilidad.sh
```

La auditoría de manifiestos y reglas se ejecuta mediante `k8s-validate` con kubeconform y promtool. La demostración del disparo y recuperación de alertas todavía está pendiente.

**Evidencias remotas relacionadas**

- [Run 45052748283 — controles y despliegues temporales exitosos](https://github.com/emiliazx/devops_007d_ols/actions/runs/45052748283)
- [Run 45077004638 — dashboard generado con datos reales de Prometheus y JaCoCo](https://github.com/emiliazx/devops_007d_ols/actions/runs/45077004638)

### IE2 — Despliegue automatizado del microservicio en un entorno orquestado

**Implementación verificable**

- [`k8s/`](../k8s/) contiene los manifiestos de `ms-auth`, MySQL, servicios, configuración y namespace.
- [`scripts/deploy-k8s.sh`](../scripts/deploy-k8s.sh) crea secretos efímeros, aplica los manifiestos y espera los rollouts de MySQL, `ms-auth` y Prometheus.
- [`docker-compose.yml`](../docker-compose.yml) y el job `deploy-smoke-test` cubren el despliegue temporal con Compose cuando el evento lo requiere.
- Los manifiestos declaran probes, requests/limits y restricciones de seguridad verificables por la auditoría.

**Alcance cloud**

Actualmente solo se ha verificado el despliegue temporal en Kubernetes `kind` dentro de GitHub Actions, además del smoke test temporal con Compose. No hay configuración ni evidencia suficiente para afirmar un despliegue en EKS, AWS, Render u otra nube. La aclaración del requisito cloud del IE2 queda pendiente.

**Validación reproducible**

En el runner Linux del workflow:

```bash
kubectl kustomize k8s
kubectl kustomize monitoring
./scripts/deploy-k8s.sh
```

La validación formal de manifiestos se ejecuta con kubeconform en `k8s-validate`; `kubectl kustomize` por sí solo no prueba un despliegue remoto.

**Evidencia remota relacionada**

- [Run 45052748283 — controles y despliegues temporales exitosos](https://github.com/emiliazx/devops_007d_ols/actions/runs/45052748283)

### IE3 — Dashboard de evidencias del pipeline

**Implementación verificable**

- [`scripts/generar_dashboard.py`](../scripts/generar_dashboard.py) genera el HTML y muestra `sin datos` cuando una muestra o incremento no existe; no convierte la ausencia en cero.
- El job `build-and-test` de [`ci.yml`](../.github/workflows/ci.yml) publica `target/site/jacoco/jacoco.xml` como artefacto `jacoco-report`.
- `k8s-deploy` descarga JaCoCo, mide el tiempo de despliegue, consulta Prometheus antes de borrar el clúster y publica `ie3-dashboard-${{ github.sha }}`.
- La duración se mide desde después de crear/cargar el clúster kind hasta que la comprobación de `/actuator/health` devuelve `UP`. No incluye la creación del clúster, la compilación ni la espera posterior de métricas.

**Métricas conservadas del dashboard**

| Dato del tablero | Qué mide | Qué no debe afirmarse |
|---|---|---|
| Cobertura | Contador `LINE` de `jacoco.xml` generado por Maven | No es un umbral de cobertura adicional |
| Duración kind | Desde el inicio de `deploy-k8s` después de crear/cargar el clúster hasta `health=UP` | No incluye creación del clúster, compilación ni la espera posterior de métricas |
| CPU | `process_cpu_usage`: CPU del proceso Java | No es CPU total del pod |
| Memoria | `jvm_memory_used_bytes` con `area="heap"`: heap usado de la JVM | No es memoria total del pod |
| HTTP 4xx | Tasa general 4xx y, por separado, incremento observado del código rechazado de la prueba | Un `401` o `403` de prueba no es una falla 5xx |
| HTTP 5xx | Tasa de respuestas 5xx | No se provoca artificialmente un 5xx |

**Evidencias remotas**

- [PR #39 — implementación del dashboard](https://github.com/emiliazx/devops_007d_ols/pull/39)
- [Run 45077004638 — dashboard generado con datos reales de Prometheus y JaCoCo](https://github.com/emiliazx/devops_007d_ols/actions/runs/45077004638)

### IE4 — Integración entre monitoreo, decisiones, incidentes y mejora continua

**Integración documentada**

- El despliegue aplica `ms-auth`, MySQL y Prometheus en el mismo entorno temporal; Prometheus descubre los pods mediante Kubernetes service discovery.
- `/actuator/health`, las métricas Micrometer y las alertas proporcionan señales separadas de disponibilidad, recursos, latencia y errores.
- Prometheus y sus alertas aportan información para observar y diagnosticar disponibilidad, recursos, latencia y errores; los controles de CI/CD —Snyk, Trivy, Gitleaks, pruebas, auditoría y SonarCloud— son los que bloquean automáticamente el despliegue mediante `CI gate` cuando corresponde.
- El incidente de Snyk en rojo y el bloqueo de kind se documentan en [Run 45041247591](https://github.com/emiliazx/devops_007d_ols/actions/runs/45041247591); la ejecución exitosa posterior se documenta en [Run 45052748283](https://github.com/emiliazx/devops_007d_ols/actions/runs/45052748283).
- PR #39 y el dashboard agregan trazabilidad entre SHA, ejecución, duración, cobertura y métricas observadas.

Esta sección describe la integración técnica. Las decisiones personales, conclusiones y reflexiones deben ser redactadas por cada integrante en la plantilla al final.

**Evidencias remotas relacionadas**

- [PR #37 — implementación del bloqueo del pipeline](https://github.com/emiliazx/devops_007d_ols/pull/37)
- [PR #39 — implementación del dashboard](https://github.com/emiliazx/devops_007d_ols/pull/39)
- [Run 45041247591 — fallo real de Snyk y bloqueo del despliegue](https://github.com/emiliazx/devops_007d_ols/actions/runs/45041247591)
- [Run 45052748283 — controles y despliegues temporales exitosos](https://github.com/emiliazx/devops_007d_ols/actions/runs/45052748283)

### IE5 — Cumplimiento, auditoría, seguridad y protección de ramas

**Implementación verificable**

- [`scripts/auditoria_cumplimiento.py`](../scripts/auditoria_cumplimiento.py) revisa manifiestos Kubernetes, seguridad de contenedores, jobs obligatorios, umbrales de Snyk/Trivy, dependencias de kind/smoke/gate, existencia de CODEOWNERS y reglas de alerta.
- `security` ejecuta Snyk sobre `pom.xml` con `--severity-threshold=high`, usando `.snyk` si existe.
- `trivy-scan` construye la imagen y falla ante vulnerabilidades `CRITICAL` detectadas por el escaneo configurado.
- `gitleaks` analiza el historial completo (`fetch-depth: 0`) y falla ante hallazgos no exceptuados.
- SonarCloud ejecuta el Quality Gate solo en push a `main`, con `sonar.qualitygate.wait=true`.
- [`README.md`](../README.md) describe los eventos, los once jobs, la diferencia entre PR, pushes de trabajo y push a `main`, y el carácter temporal de kind/Compose.
- [`scripts/demo-falla-critica.sh`](../scripts/demo-falla-critica.sh) documenta un escenario de fallo sin afirmar que Snyk y Trivy detecten necesariamente el mismo caso.
- La excepción de JAXB de [`.snyk`](../.snyk) venció el 20 de noviembre de 2026 según su fecha registrada. Debe revisarse y no se considera una remediación permanente.

**Protección y trazabilidad comprobadas**

- `develop` aparece protegida en GitHub. Esta constatación no se amplía a checks, aprobaciones u otras reglas concretas que no estén documentadas aquí.
- El push posterior al merge del PR #39 terminó exitosamente en `develop` en [Run 45193880500](https://github.com/emiliazx/devops_007d_ols/actions/runs/45193880500).

**Validación reproducible**

En Windows CMD:

```cmd
python scripts/auditoria_cumplimiento.py
```

La auditoría local no sustituye la comprobación administrativa de las reglas de GitHub ni una ejecución de los escáneres con sus credenciales y servicios reales.

**Evidencias remotas**

- [PR #37 — implementación del bloqueo del pipeline](https://github.com/emiliazx/devops_007d_ols/pull/37)
- [PR #38 — actualización y exclusión de dependencias vulnerables](https://github.com/emiliazx/devops_007d_ols/pull/38)
- [PR #39 — implementación del dashboard](https://github.com/emiliazx/devops_007d_ols/pull/39)
- [Run 45041247591 — Snyk en rojo y despliegue bloqueado](https://github.com/emiliazx/devops_007d_ols/actions/runs/45041247591)
- [Run 45052748283 — controles y despliegues temporales exitosos](https://github.com/emiliazx/devops_007d_ols/actions/runs/45052748283)
- [Run 45077004638 — artefacto del dashboard](https://github.com/emiliazx/devops_007d_ols/actions/runs/45077004638)

### IE6 — Validación automática y bloqueo ante fallas críticas

**Implementación verificable**

En [`ci.yml`](../.github/workflows/ci.yml):

- `k8s-deploy` declara `needs` sobre `build-and-test`, `security`, `gitleaks`, `docker-build`, `compliance`, `k8s-validate`, `trivy-scan` y `sonarcloud`; exige `success` en los controles aplicables.
- En un push a `main`, también exige que SonarCloud termine en `success`.
- `deploy-smoke-test` solo aplica a Pull Requests y a `main`; en PR no exige SonarCloud.
- `CI gate` usa `if: ${{ always() }}`, depende de todos los resultados que evalúa y falla si un job obligatorio termina en un resultado distinto de `success`, incluido `skipped`.
- El kind temporal y el smoke test no se ejecutan como consecuencia de un control previo fallido.

**Evidencias remotas**

- [PR #37 — implementación del bloqueo del pipeline](https://github.com/emiliazx/devops_007d_ols/pull/37)
- [Run 45041247591 — fallo real de Snyk y bloqueo del despliegue](https://github.com/emiliazx/devops_007d_ols/actions/runs/45041247591)
- [Run 45052748283 — controles y despliegues temporales exitosos](https://github.com/emiliazx/devops_007d_ols/actions/runs/45052748283)

**Validación reproducible**

En Windows CMD:

```cmd
python scripts/auditoria_cumplimiento.py
git diff --check
```

La auditoría actual comprueba la presencia de los jobs, los umbrales, las dependencias y la evaluación final del gate.

## Evidencias pendientes

Estas piezas no se deben presentar como comprobadas hasta obtenerlas:

- Capturas o artefactos de logs de `ms-auth` y MySQL, redactados y sin credenciales ni datos de usuarios.
- Pruebas de disparo y recuperación de las alertas de Prometheus, incluyendo qué estado se observó y durante cuánto tiempo.
- Aclaración de la rúbrica sobre el requisito cloud del IE2. El código actual demuestra `kind` temporal en GitHub Actions, no EKS ni AWS.
- Capturas del artefacto `ie3-dashboard-*` y de los jobs relevantes en [Run 45077004638](https://github.com/emiliazx/devops_007d_ols/actions/runs/45077004638), aunque el run se documenta como exitoso.
- Confirmación de la revisión de la excepción vencida de `.snyk`; no debe renovarse solo para hacer verde el pipeline.

## Plantilla de autoría personal

Completar individualmente por cada integrante. No copiar estas secciones entre personas ni convertirlas en una descripción generada automáticamente.

### Integrante: ____________________

**Decisiones técnicas personales**

> Escribir aquí qué decisión técnica tomé, qué alternativa descarté y por qué.

______________________________________________________________________________

______________________________________________________________________________

**Conclusiones personales**

> Escribir aquí qué evidencia considero suficiente, qué resultado obtuve y qué limitación permanece.

______________________________________________________________________________

______________________________________________________________________________

**Reflexión de aprendizaje**

> Escribir aquí qué aprendí, qué dificultad enfrenté y qué haría distinto en una siguiente iteración.

______________________________________________________________________________

______________________________________________________________________________

### Integrante: ____________________

**Decisiones técnicas personales**

> Escribir aquí qué decisión técnica tomé, qué alternativa descarté y por qué.

______________________________________________________________________________

______________________________________________________________________________

**Conclusiones personales**

> Escribir aquí qué evidencia considero suficiente, qué resultado obtuve y qué limitación permanece.

______________________________________________________________________________

______________________________________________________________________________

**Reflexión de aprendizaje**

> Escribir aquí qué aprendí, qué dificultad enfrenté y qué haría distinto en una siguiente iteración.

______________________________________________________________________________

______________________________________________________________________________

## Checklist antes de entregar

- [ ] Cada enlace remoto fue abierto y tiene una captura asociada.
- [ ] Las capturas no contienen tokens, contraseñas, logs crudos ni datos de usuarios.
- [ ] El artefacto del dashboard se descargó desde el run indicado.
- [ ] Se diferenció `kind` temporal de cualquier despliegue cloud.
- [ ] Se completaron las decisiones, conclusiones y reflexiones con autoría personal.
- [ ] Se revisaron las evidencias pendientes, incluida la protección real de `main`.
