# Build stage
FROM maven:3.8.7-eclipse-temurin-17 AS build
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
FROM eclipse-temurin:17-jre
WORKDIR /app

# Create volume for configuration
VOLUME /app/config

# Copy the built JAR file
COPY --from=build /app/target/*.jar app.jar

# Create a non-root user
RUN addgroup --system --gid 1001 appuser \
    && adduser --system --uid 1001 --ingroup appuser appuser
USER appuser

# Run the application with system properties directly
ENTRYPOINT ["java", \
            "-Dspring.datasource.url=jdbc:postgresql://postgres:5432/librarydb", \
            "-Dspring.datasource.username=postgres", \
            "-Dspring.datasource.password=postgres", \
            "-Dapi.key.header.name=X-API-KEY", \
            "-Dapi.key.admin=admin-api-key-123", \
            "-Dapi.key.books=books-api-key-456", \
            "-Dapi.key.authors=authors-api-key-789", \
            "-Dapi.key.borrowed-books=borrowed-books-api-key-101", \
            "-Dspring.jpa.hibernate.ddl-auto=update", \
            "-Dspring.jpa.show-sql=true", \
            "-Dspring.jpa.properties.hibernate.format_sql=true", \
            "-Dspring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect", \
            "-jar", "app.jar"] 