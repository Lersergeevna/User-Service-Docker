FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /app

COPY pom.xml .
COPY api-gateway/pom.xml api-gateway/pom.xml

RUN mvn -f api-gateway/pom.xml dependency:go-offline

COPY api-gateway api-gateway

RUN mvn -f api-gateway/pom.xml package -Dmaven.test.skip=true

FROM eclipse-temurin:21-jre
WORKDIR /app

COPY --from=build /app/api-gateway/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]