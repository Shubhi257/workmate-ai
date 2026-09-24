# Use Java 21 runtime
FROM eclipse-temurin:21-jre

# Set working directory inside the container
WORKDIR /app

# Copy the Spring Boot JAR into the container
COPY target/workmate-ai-*.jar app.jar

# Application port
EXPOSE 8080

# Start the Spring Boot application
ENTRYPOINT ["java", "-jar", "app.jar"]