# Use a Maven base image to build the Spring Boot application
FROM maven:3.8.4-openjdk-17-slim AS build
# Set the working directory to /app
WORKDIR /app
# Copy the POM file for dependency resolution
COPY pom.xml .
# Copy the source code
COPY src ./src
# Build the application using Maven
RUN mvn package -DskipTests

# Use a Java base image for the runtime environment
FROM openjdk:17-alpine AS runtime
# Set the working directory to /app
WORKDIR /app
# Copy the JAR file from the build stage
COPY --from=build /app/target/bitespeed-assignment-0.0.1-SNAPSHOT.jar .
# Run the Spring Boot application when the container starts
CMD ["java", "-jar", "bitespeed-assignment-0.0.1-SNAPSHOT.jar"]
