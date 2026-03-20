# Multi-stage build for Render
FROM eclipse-temurin:21-jdk AS build
WORKDIR /app

# Copy Maven wrapper and pom.xml
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# Download dependencies (cached layer)
RUN chmod +x mvnw && ./mvnw dependency:go-offline -B

# Copy only main source code (not tests)
COPY src/main ./src/main

# Build the application (skip tests)
RUN ./mvnw clean package -DskipTests -Dmaven.test.skip=true

# Runtime stage
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

# Copy the built jar from build stage
COPY --from=build /app/target/*.jar app.jar

# Expose port
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
