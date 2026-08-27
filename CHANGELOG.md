# Historial de Cambios

Este archivo registra los cambios relevantes realizados en el microservicio de autenticación.

Mientras los cambios permanezcan en desarrollo y no hayan sido integrados en una versión estable de `main`, se documentarán bajo la sección **[Sin publicar]**.

---

## [Sin publicar]

Actualmente no existen cambios pendientes para una próxima versión.

---

## [1.0.1] - 2026-08-26

### Corregido

- Se corrigió la validación del prefijo `Bearer ` en el encabezado de autorización para procesar correctamente los tokens JWT.

---

## [1.0.0] - 2026-08-26

### Añadido

- Se agregó un endpoint para consultar información básica del microservicio.
- Se agregaron validaciones para los datos recibidos durante el inicio de sesión.
- Se agregó validación para impedir el registro de usuarios o administradores utilizando un correo electrónico ya registrado.
- Se agregaron pruebas asociadas a la validación de correos electrónicos duplicados.
- Se agregó un pipeline de integración continua mediante GitHub Actions.
- Se configuró el pipeline para utilizar JDK 21 y Maven Wrapper.
- Se configuró la ejecución automática de compilación y pruebas mediante `./mvnw clean verify`.
- Se agregó documentación del proyecto mediante `README.md`, `CONTRIBUTING.md` y `CHANGELOG.md`.

### Cambiado

- Se mejoró la estructura y legibilidad del filtro de autenticación JWT.
- Se corrigió el nombre de `JwAuthenticationFilter` a `JwtAuthenticationFilter`.
- Se mejoraron los nombres de variables utilizados dentro del filtro JWT.
- Se reemplazaron impresiones directas mediante `System.out.println` por registros de log.
- Se actualizó el manejo de correos duplicados para responder de forma consistente con un conflicto HTTP `409`.
- Se actualizó la documentación del endpoint de registro para reflejar el comportamiento ante datos duplicados.

### Automatización

- Se configuró GitHub Actions para ejecutarse ante pushes hacia `develop`.
- Se configuró GitHub Actions para ejecutarse ante Pull Requests hacia `main`.
- Se agregaron validaciones adicionales para Pull Requests hacia `develop`.
- Se agregaron ejecuciones sobre ramas `feature/**` y `ci/**` para validar cambios antes de su integración.

### Documentación

- Se documentaron las convenciones utilizadas para nombrar ramas.
- Se documentó la convención utilizada para los mensajes de commit.
- Se documentó el flujo de Pull Requests y revisiones de código.
- Se documentó la estrategia de integración continua.
- Se documentó el esquema de versionamiento mediante tags.
- Se agregó una guía de contribución para mantener un flujo de trabajo consistente entre los integrantes.