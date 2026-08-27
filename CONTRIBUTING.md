# Contribución

Este documento define las convenciones y requisitos para contribuir al microservicio de autenticación.

El objetivo es mantener cambios pequeños, trazables y revisables, evitando modificaciones directas sobre las ramas principales.

---

## Estrategia de ramas

El proyecto utiliza un flujo basado en GitFlow.

### Ramas principales

- `main`: contiene versiones estables del proyecto.
- `develop`: contiene los cambios integrados que formarán parte de la siguiente versión.

No se realizan cambios directos sobre `main` ni `develop`.

### Ramas de trabajo

Las ramas deben utilizar minúsculas y palabras separadas por guiones.

Formato:

`tipo/descripcion-del-cambio`

Tipos utilizados:

- `feature/`: nuevas funcionalidades.
- `hotfix/`: correcciones urgentes sobre una versión estable.
- `refactor/`: mejoras internas sin cambio funcional.
- `docs/`: documentación.
- `ci/`: integración continua.
- `test/`: cambios asociados principalmente a pruebas.
- `chore/`: mantenimiento o configuración.

Ejemplos:

- `feature/agregar-validacion-login`
- `feature/evitar-email-duplicado`
- `hotfix/corregir-prefijo-bearer`
- `refactor/mejorar-filtro-jwt`
- `docs/documentar-proyecto`
- `ci/configurar-integracion-continua`

Las ramas `feature`, `refactor`, `docs`, `ci` y `test` se crean desde `develop`.

Las ramas `hotfix` se crean desde `main`.

---

## Alcance de los cambios

Cada rama y cada Pull Request debe tener un objetivo claramente definido.

No se deben incluir cambios que no estén relacionados con el propósito de la rama.

Si durante el desarrollo se detecta una mejora o problema independiente, debe trabajarse en una nueva rama.

Los Pull Requests pequeños y enfocados facilitan la revisión, reducen conflictos y mejoran la trazabilidad.

---

## Convención de commits

Los commits utilizan el formato:

`tipo: descripcion`

Tipos aceptados:

- `feat:` nueva funcionalidad.
- `fix:` corrección de errores.
- `test:` pruebas.
- `docs:` documentación.
- `ci:` integración continua.
- `refactor:` mejora interna sin cambio funcional.
- `chore:` mantenimiento o configuración.

Ejemplos:

- `feat: agregar validacion de datos en login`
- `fix: corregir validacion del prefijo bearer`
- `test: agregar pruebas para correos duplicados`
- `ci: configurar pipeline de integracion continua`
- `refactor: mejorar estructura del filtro jwt`
- `docs: actualizar guia de contribucion`

Los mensajes deben describir el cambio realizado y evitar descripciones genéricas como `cambios`, `prueba`, `avance` o `arreglo`.

---

## Pull Requests

Todo cambio debe integrarse mediante Pull Request.

Flujos principales:

- `feature/*` → `develop`
- `refactor/*` → `develop`
- `docs/*` → `develop`
- `ci/*` → `develop`
- `develop` → `main`
- `hotfix/*` → `main`

Cada Pull Request debe incluir:

- Un título que describa claramente el cambio.
- Una descripción del objetivo.
- Un resumen de los cambios realizados.
- La forma en que fue validado.
- Un alcance limitado al propósito de la rama.

Antes del merge se requiere:

- Pipeline de GitHub Actions exitoso.
- Ausencia de conflictos pendientes.
- Revisión de otro integrante del equipo.
- Aprobación registrada en GitHub.

No se deben fusionar Pull Requests con validaciones fallidas.

---

## Revisión de código

La persona que desarrolla el cambio no debe ser la única responsable de validarlo.

El otro integrante del equipo debe revisar:

- Que el cambio corresponda al objetivo del Pull Request.
- Que no existan modificaciones ajenas al alcance.
- Que el código sea comprensible y consistente con el proyecto.
- Que las pruebas existentes continúen funcionando.
- Que se agreguen pruebas cuando el cambio lo requiera.
- Que la documentación sea actualizada cuando corresponda.

Las observaciones deben registrarse directamente en GitHub.

La aprobación final debe quedar registrada mediante la revisión del Pull Request.

---

## Integración continua

El proyecto utiliza GitHub Actions como mecanismo de integración continua.

El workflow se encuentra en:

`.github/workflows/ci.yml`

Actualmente valida el proyecto utilizando:

- Ubuntu.
- JDK 21.
- Maven Wrapper.
- `./mvnw clean verify`

El pipeline se ejecuta en los eventos definidos en el workflow, incluyendo:

- Push hacia `develop`.
- Pull Requests hacia `develop`.
- Pull Requests hacia `main`.

Los cambios no deben integrarse si el pipeline falla.

---

## Pruebas

Los cambios que modifiquen comportamiento deben incluir o actualizar pruebas cuando sea razonable.

Antes de integrar código se espera que:

`./mvnw clean verify`

finalice correctamente.

Las correcciones de errores deberían incluir una prueba que permita comprobar el comportamiento corregido y reducir la posibilidad de regresiones.

---

## Estructura del repositorio

La estructura principal del proyecto debe mantenerse organizada de acuerdo con las responsabilidades de cada componente.

```text
src/
├── main/
│   ├── java/
│   │   └── costuras/authentication/
│   │       ├── controller/
│   │       ├── service/
│   │       ├── repository/
│   │       ├── security/
│   │       ├── model/
│   │       └── excepciones/
│   └── resources/
└── test/
    └── java/
```

Los nuevos archivos deben ubicarse en el paquete correspondiente a su responsabilidad.

No se deben crear carpetas adicionales sin una necesidad clara.

---

## Versionamiento

Las versiones estables se identifican mediante tags sobre `main`.

Formato:

`vMAJOR.MINOR.PATCH`

Ejemplos:

- `v1.0.0`: primera versión estable.
- `v1.0.1`: corrección compatible.
- `v1.1.0`: nueva funcionalidad compatible.
- `v2.0.0`: cambio incompatible relevante.

Los tags se crean únicamente después de que los cambios correspondientes hayan sido integrados a `main`.

---

## Hotfix

Los errores detectados en una versión estable se corrigen mediante ramas `hotfix/*` creadas desde `main`.

El flujo es:

`main → hotfix/* → Pull Request → main`

Después de integrar el hotfix, la corrección debe sincronizarse nuevamente con `develop`.

---

## Documentación

La documentación debe mantenerse alineada con el estado real del proyecto.

Archivos principales:

- `README.md`: descripción, configuración, ejecución y decisiones generales del proyecto.
- `CONTRIBUTING.md`: reglas de colaboración.
- `CHANGELOG.md`: cambios agrupados por versión.

Los cambios que afecten comportamiento, configuración, CI/CD o procesos de colaboración deben actualizar la documentación correspondiente.

---

## Changelog

Los cambios todavía no liberados se documentan bajo:

`[Sin publicar]`

Cuando se publica una versión estable y se crea su tag, los cambios correspondientes se trasladan a una sección versionada, por ejemplo:

`[1.0.0]`

---

## Principio de trabajo

**Una rama debe representar un objetivo y cada commit debe representar un cambio lógico relacionado con ese objetivo.**

El repositorio debe permitir entender la evolución del proyecto a través de sus ramas, commits, Pull Requests, revisiones, Actions y versiones.