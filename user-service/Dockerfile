FROM maven:3.9.11-eclipse-temurin-21

WORKDIR /app

COPY pom.xml ./pom.xml
COPY src ./src

RUN mvn clean test -DskipTests

CMD ["mvn", "exec:java"]