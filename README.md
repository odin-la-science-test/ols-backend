# OLS-backend (Odin La Science)

The core REST API for the **Odin La Science** platform.
Built with **Spring Boot 3** and **PostgreSQL**.

## 🛠 Tech Stack

* **Language:** Java 21 (LTS)
* **Framework:** Spring Boot 3.x
* **Database:** PostgreSQL 16+
* **Build Tool:** Maven

## 📋 Prerequisites

Ensure you have the following installed locally:
* Java JDK 21
* PostgreSQL
* Git

## 🗄️ Database Setup

The application requires a PostgreSQL database named `ols`.

### 1. Start PostgreSQL
Ensure your PostgreSQL service is running.
# OLS-backend — Quick commands

Essential commands to generate and access OpenAPI (springdoc) and JaCoCo reports.

Run app (dev):
```powershell
./mvnw clean package spring-boot:run
```
```powershell
mvn clean package spring-boot:run
```

OpenAPI / Swagger
- Generate / fetch the OpenAPI JSON (app must be running):
  - curl:
    ```bash
    curl -sS http://localhost:8080/v3/api-docs -o openapi.json
    ```
  - PowerShell:
    ```powershell
    Invoke-RestMethod "http://localhost:8080/v3/api-docs" -UseBasicParsing | ConvertTo-Json -Depth 99 > openapi.json
    ```
- Access Swagger UI:
  - URL: `http://localhost:8080/swagger-ui.html`
  - Open from PowerShell: `start http://localhost:8080/swagger-ui.html`

JaCoCo (coverage)
- Generate the report (runs tests):
  ```powershell
  mvn -B verify
  ```
- Open HTML report:
  ```powershell
  start target/site/jacoco/index.html
  ```