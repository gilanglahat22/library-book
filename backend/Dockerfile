# Build stage
FROM maven:3.9.5-eclipse-temurin-17-focal AS build
WORKDIR /app

# Copy pom.xml first to cache dependencies
COPY pom.xml .

# Download dependencies (this layer will be cached if pom.xml doesn't change)
RUN mvn dependency:go-offline

# Copy source code
COPY src ./src

# Show directory structure before build
RUN echo "Directory structure before build:" && \
    ls -la && \
    echo "Source directory contents:" && \
    ls -la src/main/java/com/library

# Build the application
RUN mvn clean package -DskipTests

# Show build results
RUN echo "Build results:" && \
    ls -la target/ && \
    echo "JAR file details:" && \
    find /app -name "*.jar" -type f -ls && \
    echo "JAR file contents:" && \
    jar tvf target/library-management-0.0.1-SNAPSHOT.jar

# Runtime stage
FROM eclipse-temurin:17-jre-focal
WORKDIR /app

# Create target directory
RUN mkdir -p target

# Copy the built JAR from build stage
COPY --from=build /app/target/library-management-0.0.1-SNAPSHOT.jar app.jar

# Make the JAR executable
RUN chmod +x app.jar

# Expose port
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"] 