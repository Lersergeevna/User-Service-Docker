FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /app

COPY pom.xml .
COPY discovery-service/pom.xml discovery-service/pom.xml

RUN mvn -f discovery-service/pom.xml dependency:go-offline

COPY discovery-service discovery-service

RUN mvn -f discovery-service/pom.xml package -Dmaven.test.skip=true

FROM eclipse-temurin:21-jre
WORKDIR /app

COPY --from=build /app/discovery-service/target/*.jar app.jar

EXPOSE 8761

ENTRYPOINT ["java", "-jar", "app.jar"]