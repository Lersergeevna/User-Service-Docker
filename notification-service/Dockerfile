FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /app

COPY pom.xml .
COPY notification-service/pom.xml notification-service/pom.xml

RUN mvn -f notification-service/pom.xml dependency:go-offline

COPY notification-service notification-service

RUN mvn -f notification-service/pom.xml package -Dmaven.test.skip=true

FROM eclipse-temurin:21-jre
WORKDIR /app

COPY --from=build /app/notification-service/target/*.jar app.jar

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "app.jar"]