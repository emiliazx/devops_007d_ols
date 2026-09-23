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

Como equipo seleccionamos **GitFlow** porque nos pareció una estrategia ordenada y fácil de seguir una vez que se comprende la función de cada rama. La separación entre `main`, `develop`, ramas `feature/*` y ramas `hotfix/*` permite que todos los integrantes trabajen siguiendo las mismas reglas y que sea más sencillo entender desde fuera cómo ha evolucionado el proyecto. Además, permite relacionar de manera clara el desarrollo de nuevas funcionalidades, la integración de cambios, las versiones estables y las correcciones realizadas después de una publicación.

Una de las principales ventajas que encontramos fue separar `main` de `develop`. La rama `main` representa el estado estable del proyecto, mientras que `develop` funciona como el punto donde se integran los cambios que provienen de las distintas ramas de trabajo. El desarrollo no se realiza directamente sobre `develop`, sino en ramas separadas; posteriormente, los cambios se incorporan mediante Pull Requests. De esta manera, `develop` permite comprobar cómo funcionan en conjunto diferentes modificaciones antes de integrarlas en `main`. Si aparece un problema durante esta etapa, la versión estable almacenada en `main` no se ve afectada.

Las ramas `feature/*` también fueron importantes para mantener separado cada cambio. Durante el proyecto utilizamos distintas ramas para funcionalidades específicas y, cuando cada cambio estuvo preparado, se integró mediante un Pull Request hacia `develop`. Esto permitió revisar cada modificación por separado y posteriormente comprobar cómo se comportaba junto con los demás cambios ya integrados. Cuando el conjunto de cambios en `develop` estuvo estable y las validaciones terminaron correctamente, se realizó un nuevo Pull Request desde `develop` hacia `main`.

El uso de una rama `hotfix/*` resultó útil porque permitió solucionar un problema detectado después de haber creado una versión estable en `main`. En nuestro caso, la rama `hotfix/corregir-prefijo-bearer` nació directamente desde `main`, ya que la corrección correspondía a un error presente en la versión publicada y debía solucionarse sin depender de otros cambios que pudieran encontrarse en desarrollo. Después de integrar la corrección se creó una nueva versión estable y posteriormente el cambio se sincronizó nuevamente con `develop` para evitar que futuras versiones volvieran a contener el mismo problema.

Frente a **GitHub Flow**, la principal diferencia es que GitFlow mantiene una rama `develop` permanente entre las ramas de trabajo y `main`. En GitHub Flow normalmente las ramas de cambios nacen desde la rama principal y regresan directamente a ella mediante Pull Requests. Para este proyecto preferimos contar con `develop` como punto de integración previo, porque nos permitió reunir y validar varias funcionalidades antes de incorporarlas a la versión estable.

**Trunk-Based Development** también podría haber sido utilizado, pero habría requerido una forma de trabajo diferente. Este modelo promueve integraciones mucho más frecuentes hacia una rama principal, utilizando ramas de muy corta duración y apoyándose fuertemente en pruebas y automatización. Consideramos que para nuestra forma de trabajo GitFlow fue más conveniente porque nos permitió separar de manera más visible las funcionalidades en desarrollo, la integración de cambios y las versiones estables. Trunk-Based Development no necesariamente habría producido más errores, pero sí habría requerido una mayor frecuencia de integración y una disciplina distinta a la que utilizamos durante esta evaluación.

En conclusión, GitFlow se adaptó bien al proyecto porque nos permitió mantener un flujo ordenado y trazable desde el desarrollo de una funcionalidad hasta su incorporación en una versión estable. La separación entre ramas hizo más sencillo identificar el propósito de cada cambio, revisar el trabajo realizado por cada integrante y controlar cuándo un conjunto de modificaciones estaba preparado para llegar a `main`.

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

## Hotfix realizado

Durante el proyecto se utilizó la rama:

`hotfix/corregir-prefijo-bearer`

El hotfix se creó después de publicar la primera versión estable `v1.0.0`, al detectar un problema en la validación del encabezado `Authorization` utilizado por el filtro JWT.

La validación comprobaba el prefijo mediante:

`startsWith("Bearer")`

lo que no exigía explícitamente el espacio que debe separar el esquema `Bearer` del token.

La corrección modificó esta validación para utilizar:

`startsWith("Bearer ")`

incluyendo el espacio correspondiente.

El cambio se desarrolló en la rama `hotfix/corregir-prefijo-bearer` y posteriormente se integró en `main` mediante un Pull Request, pasando previamente por revisión y validación con GitHub Actions.

Después de realizar el merge en `main`, la corrección quedó registrada como la versión:

`v1.0.1`

Finalmente, el cambio fue sincronizado nuevamente con `develop` para mantener la rama de desarrollo actualizada con la corrección aplicada sobre la versión estable.

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

## Cambios colaborativos realizados

Durante el desarrollo del proyecto se realizaron cambios mediante ramas independientes y Pull Requests antes de su integración.

| Tipo | Rama | Cambio realizado | Integración |
|---|---|---|---|
| Feature | `feature/agregar-endpoint-info` | Incorporación de un endpoint para consultar información básica del microservicio. | Pull Request hacia `develop` |
| Feature | `feature/agregar-validacion-login` | Incorporación de validaciones sobre los datos recibidos durante el inicio de sesión. | Pull Request hacia `develop` |
| Feature | `feature/evitar-email-duplicado` | Validación para impedir registros utilizando correos electrónicos ya existentes. | Pull Request hacia `develop` |
| CI | `ci/configurar-integracion-continua` | Configuración del workflow de GitHub Actions para compilar el proyecto y ejecutar pruebas automáticamente con Java 21 y Maven Wrapper. | Pull Request hacia `develop` |
| Refactor | `refactor/mejorar-filtro-jwt` | Mejora de la legibilidad y mantenibilidad del filtro JWT, incluyendo la corrección del nombre de la clase y el reemplazo de impresiones directas por registros de log. | Pull Request hacia `develop` |
| Documentación | `docs/documentar-proyecto` | Incorporación y organización de la documentación principal del repositorio mediante `README.md`, `CONTRIBUTING.md` y `CHANGELOG.md`. | Pull Request hacia `develop` |
| Hotfix | `hotfix/corregir-prefijo-bearer` | Corrección de la validación del prefijo `Bearer ` utilizado para procesar tokens JWT. | Pull Request hacia `main` y posterior sincronización con `develop` |

Los cambios fueron revisados por el otro integrante del equipo y validados mediante GitHub Actions antes de realizar los merges.

---

## Comandos utilizados durante el flujo colaborativo

Durante el trabajo se utilizaron comandos Git en las distintas etapas del flujo:

- `git clone`: obtener una copia local inicial del repositorio.
- `git checkout`: cambiar entre ramas existentes.
- `git checkout -b`: crear nuevas ramas de trabajo.
- `git status`: comprobar el estado de los archivos y cambios pendientes.
- `git diff`: revisar modificaciones antes de confirmarlas.
- `git add`: preparar archivos para un commit.
- `git commit`: registrar cambios mediante commits identificables.
- `git push`: publicar commits y ramas en GitHub.
- `git pull`: incorporar cambios remotos en las copias locales.
- `git fetch`: actualizar la información de ramas y referencias remotas.

Los merges se realizaron mediante Pull Requests en GitHub después de la revisión, aprobación y validación automática mediante GitHub Actions.

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

# Integración continua y validación automatizada con GitHub Actions

El proyecto utiliza GitHub Actions para automatizar la compilación, las pruebas, los análisis de seguridad y la validación del despliegue del microservicio.

El workflow se encuentra en:

`.github/workflows/ci.yml`

## Eventos del workflow

El pipeline se ejecuta automáticamente ante:

- Push hacia `develop`.
- Push hacia ramas `feature/**` y `ci/**`.
- Pull Requests dirigidos a `develop` o `main`.

Esto permite detectar errores antes de integrar los cambios en las ramas principales.

## Etapas del pipeline

El workflow ejecuta las siguientes actividades:

1. Descarga el código mediante GitHub Actions.
2. Configura Java 21 utilizando Temurin.
3. Habilita la ejecución de Maven Wrapper.
4. Compila el proyecto y ejecuta las pruebas con `./mvnw clean verify`.
5. Construye la imagen Docker del microservicio.
6. Instala Snyk CLI.
7. Analiza las dependencias mediante Snyk Open Source.
8. Analiza el código fuente mediante Snyk Code.
9. Genera variables de entorno temporales para las pruebas.
10. Levanta Spring Boot y MySQL mediante Docker Compose.
11. Espera a que los contenedores estén saludables.
12. Comprueba el endpoint `/actuator/health`.
13. Limpia el entorno de prueba y elimina sus volúmenes temporales.

## Análisis de seguridad

El pipeline utiliza Snyk para comprobar las dependencias y el código fuente.

Los análisis se ejecutan con un umbral de severidad alta:

```bash
snyk test --file=pom.xml --severity-threshold=high
snyk code test --severity-threshold=high
```

Los hallazgos de severidad alta o crítica pueden bloquear el despliegue de prueba.

El repositorio utiliza un secreto de GitHub Actions denominado `SNYK_TOKEN` para autenticar los análisis sin almacenar el token en el código fuente.

También se configuró Dependabot para revisar semanalmente las dependencias de Maven, las imágenes Docker y las acciones utilizadas en GitHub Actions.

## Despliegue temporal y healthchecks

Después de superar las validaciones de compilación y seguridad, GitHub Actions crea un archivo `.env` temporal con credenciales generadas para esa ejecución.

Posteriormente, utiliza Docker Compose para construir y levantar el microservicio junto con MySQL.

La comprobación principal se realiza mediante:

```bash
curl http://localhost:8080/actuator/health
```

El estado esperado de la aplicación es `UP`.

Los contenedores disponen de healthchecks y límites de CPU y memoria. El despliegue espera a que los servicios estén saludables antes de considerarse exitoso.

En este entorno de prueba, Eureka permanece instalado, pero su registro se deshabilita mediante `EUREKA_ENABLED=false`. La conexión con un servidor Eureka externo debe validarse por separado en el entorno integrado.

Al finalizar la ejecución, el pipeline elimina los contenedores y volúmenes temporales utilizados durante las pruebas.

## Alcance de CI/CD

El workflow implementa integración continua y una validación automatizada del despliegue en un entorno temporal.

No realiza un despliegue automático hacia producción.

Una ejecución exitosa confirma que el proyecto compiló, superó las pruebas habilitadas, pasó los controles de seguridad configurados y consiguió iniciar correctamente los servicios en Docker.

La integración de los cambios se realiza mediante Pull Requests y revisión del otro integrante del equipo, siguiendo la estrategia GitFlow.

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

# Docker

El proyecto incluye un `Dockerfile` multi-etapa (build con Maven + imagen final `eclipse-temurin:21-jre-alpine`) y un `docker-compose.yml` que levanta el microservicio junto a su base de datos MySQL.

## Servicios definidos en `docker-compose.yml`

- **db**: instancia de `mysql:8.0` con un volumen persistente (`db_data`) y un `healthcheck` mediante `mysqladmin ping`.
- **app**: construye la imagen a partir del `Dockerfile` local y espera a que `db` esté saludable (`depends_on: condition: service_healthy`) antes de arrancar.

## Variables de entorno

Para ejecutar el microservicio mediante Docker Compose es necesario configurar las variables de entorno utilizadas por la aplicación y la base de datos.

El repositorio incluye un archivo `.env.example` que contiene las variables requeridas y sirve como plantilla de configuración.

El archivo `.env` contiene los valores reales de cada entorno y no debe subirse al repositorio, ya que puede contener información sensible como contraseñas y claves JWT.

### 1. Crear el archivo de configuración

Copiar el archivo de ejemplo:

```bash
cp .env.example .env
```

En Windows, utilizando PowerShell:

```powershell
Copy-Item .env.example .env
```

### 2. Configurar las variables

Abrir el archivo `.env` y reemplazar los valores de ejemplo por los correspondientes al entorno local.

| Variable | Descripción |
|---|---|
| `DB_NAME` | Nombre de la base de datos MySQL. |
| `DB_USER` | Usuario utilizado para conectarse a MySQL. |
| `DB_PASSWORD` | Contraseña de acceso a MySQL. |
| `JWT_SECRET` | Clave secreta codificada en Base64 para firmar tokens JWT. |
| `EUREKA_ENABLED` | Habilita o deshabilita el registro en Eureka (`true` o `false`). |
| `EUREKA_URL` | Dirección del servidor Eureka. |

La configuración actual de Docker Compose utiliza el usuario `root` de MySQL, por lo que se debe mantener `DB_USER=root`.

Para ejecutar el microservicio únicamente con MySQL mediante Docker Compose, se puede utilizar `EUREKA_ENABLED=false`.

### 3. Protección de credenciales

El archivo `.env` está incluido en `.gitignore` para impedir que sus valores privados se incorporen accidentalmente al repositorio.

El archivo `.env.example` sí se mantiene versionado en GitHub, ya que contiene únicamente valores de ejemplo y permite conocer los requisitos de configuración del proyecto.

---

## Levantar el entorno

Una vez configurado el archivo `.env`, ejecutar:

```bash
docker compose up -d --build
```

Este comando construye la imagen del microservicio y levanta los contenedores de la aplicación y MySQL.

El microservicio quedará disponible en `http://localhost:8080` y MySQL en el puerto `3306`.

### Detener los contenedores

Para detener los servicios sin eliminar los datos almacenados:

```bash
docker compose down
```

Para detener los servicios y eliminar también los volúmenes:

```bash
docker compose down -v
```

**Advertencia:** el comando `docker compose down -v` elimina también el volumen de MySQL, por lo que se perderán los datos almacenados en ese volumen.

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

El historial de versiones estables del proyecto es:

- `v1.0.0`: primera versión estable del microservicio, creada después de integrar los cambios desarrollados y validados en `develop` hacia `main`.
- `v1.0.1`: versión de corrección creada después de integrar el hotfix `hotfix/corregir-prefijo-bearer` en `main`.
- `v1.1.0`: versión correspondiente a la Evaluación Parcial 2, que incorpora Docker Compose, automatización CI/CD, análisis de seguridad con Snyk, healthchecks y despliegue temporal de prueba.

La versión `v1.1.0` se prepara mediante una rama de publicación y se identifica con un tag sobre `main` después de completar las validaciones y el merge.

Los cambios correspondientes a esta versión se encuentran documentados en `CHANGELOG.md` bajo la sección `[1.1.0]`. Los cambios posteriores se registrarán bajo `[Sin publicar]`.

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

La herramienta se utilizó principalmente como apoyo para estructurar información, mejorar la presentación de textos y adaptar contenido al formato Markdown, además de las actividades específicas que se detallan a continuación.

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

### Documentación y formato Markdown

Se utilizó ChatGPT como herramienta de apoyo para organizar y presentar de mejor manera textos previamente redactados por los integrantes del equipo.

Parte de este apoyo consistió en transformar contenidos e ideas desarrolladas por el equipo a una estructura adecuada en formato Markdown, utilizando elementos como:

- Títulos y subtítulos.
- Listas.
- Tablas.
- Bloques de código.
- Formato para nombres de ramas, comandos y archivos.
- Organización visual de las distintas secciones del documento.

Este apoyo se utilizó principalmente en la documentación relacionada con:

- `README.md`
- `CONTRIBUTING.md`
- `CHANGELOG.md`

También se utilizó la herramienta para mejorar aspectos de claridad, organización y redacción de algunos textos ya desarrollados por los integrantes.

El contenido fue posteriormente revisado y adaptado por el equipo para comprobar que representara correctamente el trabajo realizado en el repositorio.

### Apoyo durante resolución de errores

ChatGPT también se utilizó como herramienta de orientación para interpretar mensajes de error y revisar configuraciones.

Por ejemplo, durante la configuración inicial de GitHub Actions se utilizó como apoyo para identificar una incompatibilidad entre la versión de Java configurada en el pipeline y la versión Java 21 requerida por el proyecto.

Las correcciones fueron implementadas y verificadas por los integrantes mediante nuevas ejecuciones del workflow.

### Validación y responsabilidad

Las sugerencias entregadas por la herramienta fueron revisadas, comprendidas y validadas antes de ser incorporadas.

Las modificaciones al repositorio fueron realizadas, verificadas y versionadas por los integrantes del equipo.

Las reflexiones individuales y conclusiones personales fueron redactadas directamente por cada integrante, respetando las condiciones establecidas para la evaluación.

---

## Referencias sobre uso de IA

1. OpenAI. (2026). *ChatGPT* [Modelo de lenguaje generativo].
2. Duoc UC, Bibliotecas. *Guía institucional sobre uso de Inteligencia Artificial*.

---

# Conclusiones y reflexiones individuales

## Reflexión de Emilia Zamora
Sinceramente, antes de esta evaluación usaba Git solo para guardar mi propio avance y no entendia bien cómo organizarse en equipo para no armar un enredo en el repositorio. Pasar a un flujo real me hizo cambiar el perspectiva: entendí el sentido de trabajar con ramas separadas, enviar Pull Requests a develop y ver cómo GitHub Actions testeaba el proyecto para no cometer un gran error con el código
Mi aporte estuvo enfocado en cuidar la calidad de las integraciones, apoyando en la revisión de los cambios y cuidando que los commits fueran limpios para llegar juntos a la versión final en main

La parte más compleja fue cuando estabamos trabajando en la misma ramas y se nos volvio loco el git 
no entendia que estaba pasando con el  entorno local y  el remoto. Nos complicamos un poco con los conflictos, pero con mucha investigacion logramos resolverlo, revisar con calma el árbol de commits y aprender a sincronizar bien antes de seguir subiendo cambios.

Al final me di cuenta de que Git no se trata de memorizar comandos, sino de entender la lógica detrás de cada paso. Me quedo con la tranquilidad de haberle perdido el miedo a la herramienta y saber que quedé con una base súper sólida para sumarme a cualquier equipo a futuro.



## Reflexión de Vicente Riquelme

Antes de hacer esta evaluación, no tenía claro cómo funcionaba un flujo de trabajo colaborativo real. Había usado Git de manera más individual, pero no sabía realmente cómo se organizaba un equipo para construir un proyecto juntos de manera ordenada y sin armar un caos en el repositorio.

Durante el desarrollo aprendí a usar las ramas de verdad: cómo crearlas, moverme entre ellas y llevar mis cambios a develop mediante Pull Requests. Pero más allá de memorizar los comandos, lo más valioso fue entender el porqué del flujo y la estructura. Además, ver en acción a GitHub Actions validando el proyecto en cada Pull Request fue un punto clave para mí: ahí me hizo clic lo importante que es la automatización para reducir el riesgo de integrar cambios que rompan la compilación o las pruebas.

Siento que mi principal contribución fue ayudar a establecer un orden de trabajo claro en el equipo. Me encargué de redactar el archivo CONTRIBUTING.md para fijar las pautas que seguimos. A partir de ahí, aporté de forma constante creando ramas y organizando commits, hasta que logramos integrar la primera versión estable en main identificada con el tag v1.0.0.

Una de las dificultades más grandes la tuvimos al intentar trabajar juntos directamente sobre la misma rama. Terminamos con commits cruzados e historiales desincronizados entre nuestros entornos locales y el remoto. Para solucionarlo tuvimos que parar un segundo, leer documentación, entender qué estaba pasando con el árbol de commits, organizarnos con el orden de los git push y ejecutar los git pull necesarios para para volver a mantener sincronizados nuestros entornos locales con el repositorio remoto.

Después de completar todo el trabajo, me di cuenta de que Git y GitHub no eran tan intimidantes como se veían al principio. Es algo que, a medida que lo vas practicando, se termina volviendo casi automático. Es súper importante aprender los comandos base, pero la diferencia real está en llegar al punto donde entiendes la lógica detrás de cada paso.

Con lo que más me quedo es con haberle perdido el miedo a usar Git y GitHub. Sé que me falta un montón por aprender y que esto es solo el comienzo, pero siento que quedé con una base bastante sólida. Esto me da mucha más confianza para desenvolverme en entornos de trabajo reales y coordinarme con equipos más grandes a futuro.

---

# Principio de trabajo

Durante el proyecto se mantiene la siguiente regla:

**Una rama representa un objetivo y cada commit representa un cambio lógico relacionado con ese objetivo.**

El repositorio debe permitir comprender la evolución del proyecto mediante:

`Ramas → Commits → Pull Requests → Reviews → GitHub Actions → Merge → Versiones`

De esta manera se mantiene un flujo de desarrollo ordenado, trazable y colaborativo.