# Multi-stage build para Cara Core Dental
# Stage 1: Build
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
# Baixa dependências em cache
RUN mvn -q -e -DskipTests dependency:go-offline
COPY src ./src
RUN mvn -q -DskipTests package

# Stage 2: Runtime (JRE slim)
FROM eclipse-temurin:17-jre-alpine
ENV TZ=America/Sao_Paulo \
    JAVA_OPTS=""
WORKDIR /app
# Copia jar construído
COPY --from=build /app/target/*.jar app.jar
# Expor porta padrão
EXPOSE 8080
# Healthcheck simples
HEALTHCHECK --interval=30s --timeout=5s --start-period=40s --retries=3 CMD wget -qO- http://localhost:8080/actuator/health | grep '"status":"UP"' || exit 1
ENTRYPOINT ["sh","-c","java $JAVA_OPTS -jar app.jar"]
