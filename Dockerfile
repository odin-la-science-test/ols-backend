# Build stage
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -q
COPY src ./src
RUN mvn clean package spring-boot:repackage -DskipTests -q

# Run stage
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
COPY --from=build /app/target/OLS-backend-0.0.1-SNAPSHOT.jar app.jar

# Hugging Face utilise le port 7860 par défaut
EXPOSE 7860

# Démarrage de l'application sur le port 7860
ENTRYPOINT ["java", "-Xmx384m", "-jar", "app.jar", "--server.port=7860", "--spring.profiles.active=prod"]
