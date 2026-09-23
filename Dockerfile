
# ---- Etapa 1: build con Maven ----
FROM maven:3.9.15-eclipse-temurin-26 AS build

WORKDIR /app

COPY pom.xml .

RUN mvn -B -q dependency:go-offline || true

COPY src ./src

RUN mvn -B -q clean package -DskipTests


# ---- Etapa 2: imagen final liviana ----
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Instalar curl para el healthcheck
RUN apk add --no-cache curl

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]