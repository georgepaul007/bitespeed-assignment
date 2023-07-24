# Base image
FROM maven:3.8-openjdk-11 AS builder

# Set the working directory
WORKDIR /app

# Copy the POM file
COPY pom.xml .

# Build the project dependencies
RUN mvn dependency:go-offline -B

# Copy the source code
COPY src ./src

# Build the application
RUN mvn package -DskipTests

# Build the final image
FROM openjdk:11

# Set the working directory
WORKDIR /app

# Copy the JAR file from the builder stage
COPY --from=builder /app/target/my-spring-boot-app.jar .

# Expose the port on which the application listens
EXPOSE 8080

# Run the application
CMD ["java", "-jar", "my-spring-boot-app.jar"]
