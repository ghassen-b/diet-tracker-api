# Stage 1: Build the application
FROM maven:3.9.9 AS build

# Set the working directory in the container
WORKDIR /app

# Copy the pom.xml and download dependencies
COPY pom.xml ./
RUN mvn dependency:go-offline -B

# Copy the source code and build the application
COPY src ./src
RUN mvn package -DskipTests

# Stage 2: Create the runtime image
FROM openjdk:21-jdk-slim

# Install curl (needed to run the healthcheck)
RUN apt update && apt install curl -y

# Set the working directory in the container
WORKDIR /app

# Copy the jar file from the build stage
COPY --from=build /app/target/*.jar app.jar

# Command to run the application
ENTRYPOINT exec java -jar app.jar --spring.config.location=optional:classpath:/,optional:file:config/
