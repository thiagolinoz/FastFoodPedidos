# ========================================
# Dockerfile - FastFood Pedidos
# Hexagonal Architecture
# ========================================

## Build Stage
#FROM maven:3.9-eclipse-temurin-21 AS build
#WORKDIR /app
#COPY pom.xml .
#COPY src ./src
#RUN mvn clean package -DskipTests
#
## Runtime Stage
#FROM eclipse-temurin:21-jre-alpine
#LABEL maintainer="FastFood Pedidos Team"
#LABEL description="Microserviço de Gestão de Pedidos - Arquitetura Hexagonal"
#
## Criar usuário não-root
#RUN addgroup -S fastfood && adduser -S fastfood -G fastfood
#
## Diretório da aplicação
#WORKDIR /app
#
## Copiar JAR do build stage
#COPY --from=build /app/target/*.jar app.jar
#
## Alterar proprietário
#RUN chown -R fastfood:fastfood /app
#
## Usar usuário não-root
#USER fastfood:fastfood
#
## Expor porta
#EXPOSE 8080
#
## Health check
#HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
#  CMD wget --quiet --tries=1 --spider http://localhost:8080/actuator/health || exit 1
#
## Variáveis de ambiente padrão
#ENV JAVA_OPTS="-Xms256m -Xmx512m"
#
## Executar aplicação
#ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
FROM eclipse-temurin:21-jdk
RUN adduser postech-fastfood
USER postech-fastfood:postech-fastfood
COPY ./target/postech-fastfood.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]