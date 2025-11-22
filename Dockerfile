# Estágio 1: Build (Usando imagem oficial do Maven)
FROM maven:3.9.6-eclipse-temurin-21-alpine AS build
WORKDIR /app

# Copia apenas o pom.xml primeiro para aproveitar o cache de dependências do Docker
COPY pom.xml .
RUN mvn dependency:resolve

# Copia o código-fonte e compila
COPY src src
RUN mvn clean package -DskipTests

# Estágio 2: Runtime (Usando apenas o JRE leve para rodar)
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Copia o JAR gerado no estágio anterior
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]