
# ---- Etapa 1: build con Maven ----
FROM maven:3.9.9-eclipse-temurin-21 AS build

WORKDIR /app

COPY pom.xml .
COPY src ./src

ARG MAVEN_DOWNLOAD_ATTEMPTS=3
RUN set -eu; \
    attempt=1; \
    while [ "$attempt" -le "$MAVEN_DOWNLOAD_ATTEMPTS" ]; do \
      log="$(mktemp)"; \
      if mvn -B -q -Dmaven.wagon.http.retryHandler.count=2 clean package -DskipTests >"$log" 2>&1; then \
        cat "$log"; rm -f "$log"; exit 0; \
      else \
        status=$?; \
      fi; \
      if [ "$attempt" -ge "$MAVEN_DOWNLOAD_ATTEMPTS" ] || ! grep -Eiq '429|HTTP 5[0-9][0-9]|timed out|Connection reset|Connection refused|Temporary failure in name resolution' "$log"; then \
        cat "$log" >&2; rm -f "$log"; exit "$status"; \
      fi; \
      echo "Maven download transient failure; retry $((attempt + 1))/$MAVEN_DOWNLOAD_ATTEMPTS after $((attempt * 15))s" >&2; \
      rm -f "$log"; sleep $((attempt * 15)); attempt=$((attempt + 1)); \
    done; \
    echo "ERROR: Maven Docker build exhausted $MAVEN_DOWNLOAD_ATTEMPTS attempts" >&2; exit 1


# ---- Etapa 2: imagen final liviana ----
FROM eclipse-temurin:25-jre-alpine

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
