FROM maven:3.8.7-eclipse-temurin-17 AS build

WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline

COPY src/ /app/src/
RUN mvn package -DskipTests

FROM eclipse-temurin:17-jre

WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

# ENV SERVER_PORT=8090
# ENV SPRING_PROFILES_ACTIVE=prod
# ENV API_INTEGRATOR_BASE_URL=http://api_integrator:8080
# ENV API_INTEGRATOR_TIMEOUT=5000
# ENV ALLOWED_IPS=127.0.0.1,::1

EXPOSE 8090

ENTRYPOINT ["java", "-jar", "app.jar"] 