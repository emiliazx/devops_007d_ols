# Microservicio de Autenticación

Repositorio correspondiente al microservicio de autenticación utilizado como base para la implementación de prácticas de control de versiones, trabajo colaborativo e integración continua.

**Asignatura:** Ingeniería DevOps (DOY0101)  
**Institución:** Duoc UC  
**Repositorio:** `devops_007d_ols`  
**Integrantes:** Emilia Zamora y Vicente Riquelme  

---

## Descripción

El proyecto corresponde a un microservicio de autenticación desarrollado con Spring Boot.

Durante este trabajo el microservicio se utiliza como base para aplicar prácticas relacionadas con:

- Control de versiones mediante Git.
- Trabajo colaborativo mediante GitHub.
- Estrategias de ramificación.
- Pull Requests y revisiones de código.
- Convenciones de commits.
- Integración continua mediante GitHub Actions.
- Pruebas automatizadas.
- Versionamiento mediante tags.
- Documentación técnica del repositorio.

---

## Tecnologías utilizadas

El proyecto utiliza principalmente:

- Java 21.
- Spring Boot.
- Spring Security.
- JSON Web Token (JWT).
- Spring Data JPA.
- Maven.
- Maven Wrapper.
- Git.
- GitHub.
- GitHub Actions.
- Docker.

---

# Estrategia de ramificación

El repositorio utiliza una estrategia basada en **GitFlow**, manteniendo separadas las versiones estables, la integración de cambios y el desarrollo de nuevas funcionalidades.

Las ramas principales son:

- `main`: contiene las versiones consideradas estables.
- `develop`: contiene los cambios integrados que formarán parte de la siguiente versión.

No se realizan cambios de desarrollo directamente sobre `main` ni `develop`.

Los cambios se realizan mediante ramas independientes y posteriormente se integran mediante Pull Requests.

---

## Modelos de ramificación analizados

Durante el desarrollo de la evaluación se consideran los siguientes modelos:

### Git Flow

Utiliza ramas principales como `main` y `develop`, además de ramas específicas para funcionalidades, releases y correcciones.

Permite mantener separadas las versiones estables del trabajo que todavía se encuentra en desarrollo.

### GitHub Flow

Mantiene una rama principal estable y utiliza ramas de corta duración para realizar cambios que posteriormente se integran mediante Pull Requests.

Su flujo es más simple que GitFlow y se orienta a integraciones frecuentes.

### Trunk-Based Development

Se basa en integrar cambios frecuentemente sobre una rama principal o *trunk*.

Puede utilizar ramas de muy corta duración y depende fuertemente de automatización y pruebas para permitir una integración frecuente y segura.

---

## Justificación de la estrategia seleccionada

> **Esta sección debe ser redactada por los integrantes del equipo sin apoyo de inteligencia artificial, de acuerdo con las instrucciones de la evaluación.**
>
> En esta sección el equipo debe explicar con sus propias palabras:
>
> - Por qué seleccionó GitFlow.
> - Qué función cumple `main`.
> - Qué función cumple `develop`.
> - Cómo las ramas `feature/*` facilitaron el trabajo colaborativo.
> - Qué utilidad tienen las ramas `hotfix/*`.
> - Por qué esta estrategia resultó adecuada para este proyecto frente a las otras alternativas analizadas.

---

# Convenciones de ramas

Las ramas utilizan nombres en minúsculas, sin espacios ni tildes y con las palabras separadas mediante guiones.

Formato general:

`tipo/descripcion-del-cambio`

## Funcionalidades

Formato:

`feature/<descripcion>`

Ejemplos utilizados:

- `feature/agregar-endpoint-info`
- `feature/agregar-validacion-login`
- `feature/evitar-email-duplicado`

Las ramas `feature/*` se crean desde `develop`.

---

## Hotfix

Formato:

`hotfix/<descripcion>`

Ejemplo planificado:

- `hotfix/corregir-prefijo-bearer`

Las ramas `hotfix/*` se crean desde `main` y se utilizan para corregir errores detectados sobre una versión estable.

---

## Integración continua

Formato:

`ci/<descripcion>`

Ejemplo utilizado:

- `ci/configurar-integracion-continua`

---

## Refactorización

Formato:

`refactor/<descripcion>`

Ejemplo utilizado:

- `refactor/mejorar-filtro-jwt`

---

## Documentación

Formato:

`docs/<descripcion>`

Ejemplo utilizado:

- `docs/documentar-proyecto`

---

## Pruebas

Formato:

`test/<descripcion>`

Estas ramas pueden utilizarse cuando el objetivo principal del cambio corresponda exclusivamente a pruebas.

---

# Alcance de las ramas

Cada rama debe representar un único objetivo.

Una rama creada para desarrollar una funcionalidad no debe incluir modificaciones que no estén relacionadas con dicha funcionalidad.

Por ejemplo:

`feature/evitar-email-duplicado`

debe contener únicamente cambios asociados a impedir el registro de cuentas utilizando un correo electrónico ya existente.

Si durante el desarrollo se detecta un problema independiente, debe trabajarse en una nueva rama.

Este criterio permite mantener Pull Requests pequeños, comprensibles y fáciles de revisar.

---

# Convención de commits

El proyecto utiliza una convención basada en **Conventional Commits**.

Formato:

`tipo: descripcion`

Tipos utilizados:

- `feat:` nueva funcionalidad.
- `fix:` corrección de errores.
- `test:` creación o modificación de pruebas.
- `docs:` documentación.
- `ci:` integración continua.
- `refactor:` mejora interna sin modificar el comportamiento esperado.
- `chore:` mantenimiento o configuración.

Ejemplos utilizados durante el proyecto:

- `feat: agregar validacion de datos en login`
- `feat: evitar registro de correos duplicados`
- `test: agregar pruebas para correos duplicados`
- `ci: configurar pipeline de integracion continua`
- `ci: corregir version de java y habilitar pruebas`
- `refactor: mejorar estructura del filtro jwt`
- `docs: agregar guia de contribucion del proyecto`
- `chore: inicializar microservicio de autenticacion`

Se evitan mensajes genéricos como:

- `cambios`
- `prueba`
- `avance`
- `arreglo`
- `listo`

Los mensajes deben permitir comprender qué ocurrió sin necesidad de revisar inmediatamente el contenido completo del commit.

---

# Flujo colaborativo y trazabilidad

Todo cambio se desarrolla en una rama independiente y se integra mediante Pull Request.

Para funcionalidades se utiliza:

`develop → feature/* → Pull Request → revisión → GitHub Actions → aprobación → develop`

Para documentación:

`develop → docs/* → Pull Request → revisión → GitHub Actions → aprobación → develop`

Para cambios de integración continua:

`develop → ci/* → Pull Request → revisión → GitHub Actions → aprobación → develop`

Para refactorizaciones:

`develop → refactor/* → Pull Request → revisión → GitHub Actions → aprobación → develop`

Cuando los cambios integrados en `develop` son considerados estables se utiliza:

`develop → Pull Request → main`

Para errores encontrados sobre una versión estable:

`main → hotfix/* → Pull Request → main`

Después de integrar un hotfix en `main`, la corrección también debe sincronizarse con `develop`.

---

# Pull Requests

Todo cambio relevante debe integrarse mediante Pull Request.

Cada Pull Request debe contener:

- Un título que identifique claramente el cambio.
- El objetivo del cambio.
- Un resumen de los archivos o comportamientos modificados.
- Una explicación de cómo fue validado.
- Un alcance limitado al propósito de la rama.

Antes del merge se comprueba que:

- La rama corresponda al objetivo indicado.
- Los commits sean claros.
- No existan cambios ajenos al objetivo.
- No existan conflictos pendientes.
- GitHub Actions finalice correctamente.
- Otro integrante haya realizado la revisión.
- Exista una aprobación registrada en GitHub.

No se realizan merges con validaciones automáticas fallidas.

---

# Estrategia de revisión

La persona que desarrolla un cambio crea el Pull Request.

El otro integrante revisa los cambios utilizando la sección `Files changed` de GitHub.

La revisión queda registrada utilizando:

`Review changes → Approve`

Si se encuentra un problema se pueden utilizar comentarios o `Request changes` antes de realizar el merge.

El flujo utilizado es:

`Desarrollo → Pull Request → Revisión → GitHub Actions → Aprobación → Merge`

De esta manera GitHub conserva evidencia sobre:

- Quién realizó los commits.
- Quién creó el Pull Request.
- Quién revisó el cambio.
- El resultado de GitHub Actions.
- La aprobación.
- El merge final.

---

# Integración continua con GitHub Actions

El proyecto utiliza **GitHub Actions** para automatizar la validación del código.

El workflow se encuentra en:

`.github/workflows/ci.yml`

El pipeline utiliza:

- Ubuntu como entorno de ejecución.
- JDK 21 mediante Temurin.
- Maven Wrapper.
- Caché de dependencias Maven.
- Compilación y ejecución automática de pruebas.

La validación principal se realiza mediante:

```bash
./mvnw clean verify
```

---

## Eventos del workflow

El workflow está configurado para ejecutarse ante:

### Push

- `develop`
- `feature/**`
- `ci/**`

### Pull Request

- `develop`
- `main`

Los eventos adicionales sobre ramas de desarrollo permiten detectar problemas antes de integrar los cambios.

Los eventos requeridos para el flujo principal son:

`push → develop`

y:

`pull request → main`

---

## Proceso de validación

El pipeline ejecuta las siguientes etapas:

1. Descarga el código del repositorio.
2. Configura JDK 21.
3. Habilita la ejecución del Maven Wrapper.
4. Compila el proyecto.
5. Ejecuta las pruebas.
6. Informa si la validación fue exitosa o fallida.

Un resultado exitoso permite continuar con el proceso de revisión y merge.

Un resultado fallido indica que el cambio debe ser revisado antes de ser integrado.

---

## Rol dentro de CI/CD

El workflow implementado actualmente corresponde principalmente a **Integración Continua (CI)**.

GitHub Actions automatiza la compilación y ejecución de pruebas cada vez que ocurre uno de los eventos definidos en el workflow.

Esto permite comprobar automáticamente que los cambios puedan integrarse sin romper la compilación o las pruebas existentes.

Actualmente el pipeline no realiza un despliegue automático hacia un ambiente productivo, por lo que no representa todavía un proceso completo de Continuous Deployment.

> El equipo debe ser capaz de explicar con sus propias palabras durante la evaluación por qué esta automatización resulta útil dentro de su flujo de trabajo.

---

# Estructura del repositorio

La estructura principal se mantiene organizada según la responsabilidad de cada componente.

```text
.
├── .github/
│   └── workflows/
│       └── ci.yml
│
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── costuras/
│   │   │       └── authentication/
│   │   │           ├── controller/
│   │   │           ├── service/
│   │   │           ├── repository/
│   │   │           ├── security/
│   │   │           ├── model/
│   │   │           └── excepciones/
│   │   │
│   │   └── resources/
│   │
│   └── test/
│       └── java/
│
├── .mvn/
├── .gitignore
├── Dockerfile
├── mvnw
├── mvnw.cmd
├── pom.xml
├── README.md
├── CONTRIBUTING.md
└── CHANGELOG.md
```

Los nuevos archivos deben ubicarse en el paquete correspondiente a su responsabilidad.

No se deben crear carpetas adicionales sin una necesidad clara.

---

# Versionamiento

Las versiones estables del proyecto se identificarán mediante tags creados sobre `main`.

Se utiliza el formato:

`vMAJOR.MINOR.PATCH`

Ejemplos:

- `v1.0.0`: primera versión estable.
- `v1.0.1`: corrección sobre una versión estable.
- `v1.1.0`: incorporación de nuevas funcionalidades compatibles.
- `v2.0.0`: cambios importantes o incompatibles.

Los tags solamente se crean cuando los cambios correspondientes ya se encuentran integrados en `main`.

No se crean tags sobre ramas `feature`, `develop`, `docs`, `ci` o `refactor`.

---

## Estado actual del versionamiento

Mientras los cambios todavía no hayan sido integrados desde `develop` hacia `main`, se documentan en `CHANGELOG.md` bajo:

`[Sin publicar]`

Una vez realizada la primera integración estable hacia `main`, se podrá crear el tag:

`v1.0.0`

y actualizar el `CHANGELOG.md` para registrar oficialmente esa versión.

---

# Documentación del repositorio

El proyecto utiliza los siguientes archivos:

### README.md

Contiene la descripción general del proyecto, estrategia de trabajo, integración continua y principales convenciones.

### CONTRIBUTING.md

Contiene las reglas utilizadas para colaborar en el repositorio, incluyendo:

- Naming de ramas.
- Convención de commits.
- Pull Requests.
- Revisiones.
- Integración continua.
- Pruebas.
- Versionamiento.

### CHANGELOG.md

Registra los cambios relevantes del proyecto.

Los cambios todavía no publicados permanecen bajo:

`[Sin publicar]`

y posteriormente se agrupan bajo la versión correspondiente cuando se crea un tag estable.

---

# Uso de Inteligencia Artificial

Durante el desarrollo de la evaluación se utilizó **ChatGPT, de OpenAI**, como herramienta de apoyo.

El uso de inteligencia artificial estuvo orientado a las siguientes actividades:

### Mensajes de commit

Se utilizó ChatGPT como apoyo para estructurar y mejorar la redacción de mensajes de commit a partir de cambios ya realizados por los integrantes.

Las sugerencias fueron revisadas y ajustadas antes de realizar cada commit.

### Pull Requests

Se utilizó ChatGPT como apoyo para organizar las descripciones de Pull Requests, incluyendo:

- Objetivo.
- Cambios realizados.
- Validación.
- Resultado esperado.

El contenido de cada Pull Request fue revisado por los integrantes antes de su publicación.

### Documentación

Se utilizó ChatGPT como apoyo para organizar y mejorar la redacción de documentación relacionada con:

- `README.md`
- `CONTRIBUTING.md`
- `CHANGELOG.md`

La documentación generada con apoyo de IA fue revisada y adaptada al funcionamiento real del repositorio.

### Apoyo durante resolución de errores

ChatGPT también se utilizó como herramienta de orientación para interpretar mensajes de error y revisar configuraciones.

Por ejemplo, durante la configuración inicial de GitHub Actions se utilizó como apoyo para identificar una incompatibilidad entre la versión de Java configurada en el pipeline y la versión Java 21 requerida por el proyecto.

Las correcciones fueron implementadas y verificadas por los integrantes mediante nuevas ejecuciones del workflow.

### Validación y responsabilidad

Las sugerencias entregadas por la herramienta fueron revisadas, comprendidas y validadas antes de ser incorporadas.

Las modificaciones al repositorio fueron realizadas, verificadas y versionadas por los integrantes del equipo.

La inteligencia artificial no debe utilizarse para redactar las reflexiones individuales ni las conclusiones personales requeridas por la evaluación.

---

## Referencias sobre uso de IA

1. OpenAI. (2026). *ChatGPT* [Modelo de lenguaje generativo].
2. Duoc UC, Bibliotecas. *Guía institucional sobre uso de Inteligencia Artificial*.

---

# Conclusiones y reflexiones individuales

De acuerdo con las instrucciones de la evaluación, cada integrante debe redactar personalmente su reflexión sin apoyo de inteligencia artificial.

## Reflexión de Emilia Zamora

> **Redactar personalmente sin apoyo de IA.**
>
> La reflexión debe explicar el aprendizaje obtenido durante la evaluación y la contribución personal realizada al proyecto.

---

## Reflexión de Vicente Riquelme

> **Redactar personalmente sin apoyo de IA.**
>
> La reflexión debe explicar el aprendizaje obtenido durante la evaluación y la contribución personal realizada al proyecto.

---

# Principio de trabajo

Durante el proyecto se mantiene la siguiente regla:

**Una rama representa un objetivo y cada commit representa un cambio lógico relacionado con ese objetivo.**

El repositorio debe permitir comprender la evolución del proyecto mediante:

`Ramas → Commits → Pull Requests → Reviews → GitHub Actions → Merge → Versiones`

De esta manera se mantiene un flujo de desarrollo ordenado, trazable y colaborativo.