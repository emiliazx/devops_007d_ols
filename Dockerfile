
# ---- Etapa 1: build con Maven ----
FROM maven:3.9-eclipse-temurin-26 AS build

WORKDIR /app

COPY pom.xml .
COPY src ./src

RUN mvn -B -q clean package -DskipTests


# ---- Etapa 2: imagen final liviana ----
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Instalar curl para el healthcheck
RUN apk add --no-cache curl

# UID/GID numericos fijos: Kubernetes (runAsNonRoot) necesita un usuario numerico
RUN addgroup -S -g 10001 appgroup && adduser -S -u 10001 -G appgroup appuser

COPY --from=build /app/target/*.jar app.jar

RUN chown appuser:appgroup app.jar

USER 10001:10001

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
