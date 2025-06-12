FROM maven:3.8.7-eclipse-temurin-17 AS build

WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline

COPY src/ /app/src/
RUN mvn package -DskipTests

FROM eclipse-temurin:17-jre

WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

# Environment variables will be supplied from docker-compose.yml
EXPOSE 8090

ENTRYPOINT ["java", "-jar", "app.jar"] 