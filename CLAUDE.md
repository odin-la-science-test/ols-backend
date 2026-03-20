# OLS Backend - CLAUDE.md

## Projet

**Odin La Science (OLS)** - Plateforme d'apprentissage en sciences (bactériologie, mycologie, etc.) avec messagerie instantanée intégrée.

- **Stack** : Spring Boot 4.0.0, Java 21, PostgreSQL, Redis, MinIO, WebSocket
- **Frontend** : `../ols-frontend` (React, design atomique)
- **Build** : Maven (`./mvnw spring-boot:run -Dspring-boot.run.profiles=dev`)
- **Swagger** : http://localhost:8080/swagger-ui.html
- **Infra locale** : `docker-compose.local.yml` (PostgreSQL, Redis, MinIO)

## Architecture

Monolithe modulaire : `com.odinlascience.backend.modules.{module}`

### Modules existants
- `chat` - Messagerie instantanée (serveurs, channels, messages, WebSocket)
- `bacteriology` - Identification bactérienne
- `mycology` - Identification fongique
- `catalog` - Catalogue des modules/cours
- `common` - Classes abstraites partagées

### Couches par module (dans cet ordre)
```
model/          → Entités JPA (@Entity, @Data, @Builder)
dto/            → DTOs pour l'API (@Data, @Builder, @JsonInclude)
enums/          → Enums du domaine (optionnel)
repository/     → JpaRepository + Custom si besoin
mapper/         → MapStruct (@Mapper(componentModel = "spring"))
service/        → Logique métier (@Service, @Transactional, @RequiredArgsConstructor)
controller/     → REST API (@RestController, @RequestMapping, @Operation)
config/         → Configuration spécifique au module (optionnel)
handler/        → WebSocket handlers (optionnel)
security/       → Sécurité spécifique au module (optionnel)
```

## Ajouter un nouveau module

1. Créer le package `modules/{nom_module}/`
2. Créer les sous-packages nécessaires (model, dto, repository, service, controller, etc.)
3. Entités : `@Entity`, `@Data`, `@Builder`, `@NoArgsConstructor`, `@AllArgsConstructor`
4. Repos : extends `JpaRepository<Entity, Long>`
5. Services : `@Service`, `@Transactional`, `@RequiredArgsConstructor` + champs `final`
6. Controllers : `@RestController`, `@RequestMapping("/api/{module}")`, `@Tag(name = "...")`
7. Auth : `@AuthenticationPrincipal UserDetails user` dans les endpoints protégés
8. DTOs : `@Data`, `@Builder`, `@JsonInclude(JsonInclude.Include.NON_NULL)`
9. Mappers : MapStruct `@Mapper(componentModel = "spring")`
10. Ajouter les endpoints au `SecurityConfig` si nécessaire

## Conventions de nommage

| Élément      | Convention                           | Exemple                        |
|-------------|--------------------------------------|--------------------------------|
| Package     | `modules.{module}`                   | `modules.chat`                 |
| Entité      | PascalCase, singulier                | `Server`, `Bacterium`          |
| DTO         | PascalCase + `DTO` ou `Request`      | `ServerDTO`, `LoginRequest`    |
| Service     | PascalCase + `Service`               | `ServerService`                |
| Controller  | PascalCase + `Controller`            | `ServerController`             |
| Repository  | PascalCase + `Repository`            | `ServerRepository`             |
| Mapper      | PascalCase + `Mapper`                | `BacteriumMapper`              |
| Endpoint    | kebab-case                           | `/api/chat/servers/{serverId}` |
| Table (chat)| préfixe `chat_`                      | `chat_servers`, `chat_messages`|

## Sécurité

- JWT (access + refresh tokens) via JJWT 0.12.6
- Filtre : `JwtAuthenticationFilter` dans la chaîne Spring Security
- Support guest tokens (utilisateurs anonymes)
- Rate limiting : Bucket4j (10k req/min dev, 60 req/min prod)
- CORS : localhost:3000 (dev), odinlascience.com (prod)

## Profils

- `dev` : `ddl-auto=create-drop`, SQL verbose, data.sql chargé, rate limits permissifs
- `prod` : `ddl-auto=validate`, config par variables d'environnement

## Design atomique (frontend)

Le frontend (`../ols-frontend`) suit le design atomique :
- **atoms** : composants de base (bouton, input, label)
- **molecules** : combinaisons d'atoms (champ de formulaire, carte)
- **organisms** : sections complètes (navbar, sidebar, formulaire)
- **templates** : layouts de pages
- **pages** : pages finales avec données

## Git & Branching

### Branches
- Format : `OLS-{TICKET}-{Description-En-Kebab-Case}`
- Exemple : `OLS-001-Infrastructure-Messagerie-Instantanee`
- PR merge vers `main`

### Commits
- Format : `OLS-{TICKET} {type}({scope}): {description}`
- Ou format court sans ticket : `{type}: {description}`
- Types : `feat`, `fix`, `refactor`, `docs`, `test`, `chore`
- Scope optionnel : `(chat)`, `(lab)`, `(mycology)`, etc.
- Exemples :
  - `OLS-000 feat(lab): Added modules visually`
  - `feat: Add new API code entry for fungus in data.sql`
  - `fix: handle null case for locked property in AppModuleDTO conversion`

## Commandes utiles

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev    # Lancer en dev
./mvnw clean package -DskipTests                          # Build
docker compose -f docker-compose.local.yml up -d          # Infra locale
```
