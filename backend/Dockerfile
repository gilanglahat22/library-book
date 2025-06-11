# Build stage
FROM maven:3.8.4-openjdk-17-slim AS build
WORKDIR /app

# Copy maven files first to leverage Docker cache
COPY pom.xml .
COPY .mvn .mvn
COPY mvnw .
COPY mvnw.cmd .

# Download dependencies
RUN mvn dependency:go-offline -B

# Copy source code
COPY src src

# Build the application
RUN mvn package -DskipTests

# Runtime stage
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Create volume for configuration
VOLUME /app/config

# Default environment variables - these will be overridden by .env file
ENV API_KEY_HEADER_NAME=X-API-KEY \
    API_KEY_ADMIN=admin-api-key-123 \
    API_KEY_BOOKS=books-api-key-456 \
    API_KEY_AUTHORS=authors-api-key-789 \
    API_KEY_BORROWED_BOOKS=borrowed-books-api-key-101 \
    SPRING_JPA_HIBERNATE_DDL_AUTO=update \
    SPRING_JPA_SHOW_SQL=true \
    SPRING_JPA_FORMAT_SQL=true

# Copy the built JAR file
COPY --from=build /app/target/*.jar app.jar

# Create a non-root user
RUN addgroup --system --gid 1001 appuser \
    && adduser --system --uid 1001 --ingroup appuser appuser
USER appuser

# Run the application with explicit reference to environment variables
ENTRYPOINT ["java", \
            "-Dspring.datasource.url=${DB_URL}", \
            "-Dspring.datasource.username=${DB_USERNAME}", \
            "-Dspring.datasource.password=${DB_PASSWORD}", \
            "-Dapi.key.header.name=${API_KEY_HEADER_NAME}", \
            "-DAPI_KEY_ADMIN=${API_KEY_ADMIN}", \
            "-DAPI_KEY_BOOKS=${API_KEY_BOOKS}", \
            "-DAPI_KEY_AUTHORS=${API_KEY_AUTHORS}", \
            "-DAPI_KEY_BORROWED_BOOKS=${API_KEY_BORROWED_BOOKS}", \
            "-Dspring.jpa.hibernate.ddl-auto=${SPRING_JPA_HIBERNATE_DDL_AUTO}", \
            "-Dspring.jpa.show-sql=${SPRING_JPA_SHOW_SQL}", \
            "-Dspring.jpa.properties.hibernate.format_sql=${SPRING_JPA_FORMAT_SQL}", \
            "-jar", "app.jar"] 