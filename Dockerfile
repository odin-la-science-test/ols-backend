# Build stage
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Run stage
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
COPY --from=build /app/target/OLS-backend-0.0.1-SNAPSHOT.jar app.jar

# Hugging Face utilise le port 7860 par défaut
EXPOSE 7860

# Démarrage de l'application sur le port 7860
ENTRYPOINT ["java", "-jar", "app.jar", "--server.port=7860"]
