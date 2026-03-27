---
title: Odin La Science
emoji: 🔬
colorFrom: blue
colorTo: indigo
sdk: docker
app_port: 7860
pinned: false
---

# OLS Backend
API REST pour la plateforme **Odin La Science**.

## Stack

- **Framework** : Spring Boot 4.0.0, Java 21
- **Base de donnees** : PostgreSQL
- **Cache** : Redis
- **Stockage fichiers** : MinIO (S3-compatible)
- **Temps reel** : WebSocket (STOMP)
- **Build** : Maven (`./mvnw`)
- **Documentation API** : Swagger / OpenAPI

## Pre-requis

- Java JDK 21
- Docker (pour l'infra locale)

## Demarrage

```bash
# Lancer l'infra locale (PostgreSQL, Redis, MinIO)
docker compose -f docker-compose.local.yml up -d

# Lancer l'application en mode dev (port 8080)
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

## Commandes

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev    # Lancer en dev
./mvnw clean package -DskipTests                          # Build
./mvnw test                                               # Tous les tests
./mvnw test -Dtest=ContactServiceTest                     # Test d'une classe
./mvnw test -Dtest="ContactServiceTest#methodName"        # Test d'une methode
./mvnw verify                                             # Tests + JaCoCo coverage
```

## Swagger

- **UI** : http://localhost:8080/swagger-ui.html
- **JSON** : `curl -sS http://localhost:8080/v3/api-docs -o openapi.json`

## Architecture

Monolithe modulaire : `com.odinlascience.backend.modules.{module}`

Chaque module suit la structure : `model/ → dto/ → repository/ → mapper/ → service/ → controller/`

## Documentation

- **Regles et conventions** : [CONVENTIONS.md](CONVENTIONS.md)
- **Guide de creation de module** : [module-creation-guide.md](../OLS-documentation/module-creation-guide.md)
