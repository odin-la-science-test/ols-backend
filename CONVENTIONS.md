# OLS Backend — Conventions

Source de verite pour les regles et conventions obligatoires du backend OLS. Pour les guides pratiques et tutoriels, voir `OLS-documentation/`.

---

## Stack

- **Framework** : Spring Boot 4.0.0, Java 21
- **Base de donnees** : PostgreSQL
- **Cache** : Redis
- **Stockage fichiers** : MinIO
- **Temps reel** : WebSocket (STOMP)
- **Build** : Maven (`./mvnw`)
- **Documentation API** : Swagger / OpenAPI (`/swagger-ui.html`)
- **Infra locale** : `docker-compose.local.yml` (PostgreSQL, Redis, MinIO)

---

## Architecture

Monolithe modulaire : `com.odinlascience.backend.modules.{module}`

### Packages top-level (hors modules)

| Package | Role |
|---------|------|
| `auth` | Authentification (login, register, tokens) |
| `config` | Configuration Spring (CORS, WebSocket, etc.) |
| `exception` | Exceptions custom + GlobalExceptionHandler |
| `logging` | Configuration logging |
| `ratelimit` | Rate limiting (Bucket4j) |
| `security` | JWT filter, SecurityConfig, UserDetailsService |
| `user` | Entite User, UserRepository, UserService |

### Modules existants

| Module | Type | Description |
|--------|------|-------------|
| `bacteriology` | Science | Identification bacterienne |
| `mycology` | Science | Identification fongique |
| `catalog` | Systeme | Catalogue des modules/cours |
| `common` | Shared | Classes abstraites partagees (identification) |
| `contacts` | CRUD owned | Carnet de contacts |
| `notes` | CRUD owned | Cahier de laboratoire |
| `notifications` | Systeme | Systeme de notifications |
| `quickshare` | CRUD owned | Partage instantane de fichiers/texte |
| `organization` | Systeme | Organisations, membres, supervision |
| `support` | Systeme | Tickets de support |
| `ai` | Systeme | Correction de texte (LanguageTool) |

### Couches par module (dans cet ordre)

```
model/          -> Entites JPA
dto/            -> DTOs pour l'API
enums/          -> Enums du domaine (optionnel)
repository/     -> JpaRepository + Custom si besoin
mapper/         -> MapStruct ou manuel
service/        -> Logique metier
controller/     -> REST API
config/         -> Configuration specifique (optionnel)
handler/        -> WebSocket handlers (optionnel)
security/       -> Securite specifique (optionnel)
```

---

## Conventions generales

S'appliquent a tout le code backend.

### Taille des fichiers

- **Max ~200 lignes** par fichier source (hors tests). Au-dela, decouper en classes/services specialises.
- Les fichiers de test peuvent aller jusqu'a **~300 lignes**.
- Les `GlobalExceptionHandler` et fichiers de configuration sont toleres au-dela si chaque bloc reste simple.

### Lombok

Toutes les classes utilisent Lombok :

| Annotation | Usage |
|------------|-------|
| `@Data` | Entites, DTOs |
| `@Builder` | Entites, DTOs |
| `@NoArgsConstructor` | Entites, DTOs (requis par JPA/Jackson) |
| `@AllArgsConstructor` | Entites, DTOs |
| `@RequiredArgsConstructor` | Services, Controllers (injection par constructeur) |
| `@Slf4j` | Services (logging) |

### MapStruct

Mappers entity <-> DTO : `@Mapper(componentModel = "spring")`

Note : Lombok annotation processor doit etre configure avec MapStruct dans le `pom.xml` (binding `lombok-mapstruct-binding`).

### Validation

- `@Valid` sur les `@RequestBody` dans les controllers
- Annotations Jakarta sur les DTOs de requete : `@NotBlank`, `@Email`, `@Size`, etc.

### Logging

- `@Slf4j` (Lombok) sur chaque service
- `log.error("message", exception)` — jamais `e.printStackTrace()`
- Contexte dans les messages : userId, action, entite

### Reponses HTTP

- **DELETE** : toujours `204 No Content` (`ResponseEntity.noContent().build()`)
- **GET/POST/PUT** : `200 OK` avec body (`ResponseEntity.ok(...)`)
- **Erreurs** : gerees par `GlobalExceptionHandler` avec reponse standardisee

### Recherche

- Parametre toujours nomme `query` : `@RequestParam("query") String query`
- Jamais `q`, `search`, ou autre variante

### Swagger / OpenAPI

- `@Tag(name = "...", description = "...")` sur chaque controller
- `@Operation(summary = "...", description = "...")` sur chaque endpoint
- **Schema export** : `openapi.json` a la racine est utilise par le frontend pour generer les types TypeScript. Apres modification d'un DTO ou enum, re-exporter le schema (voir workflow dans `OLS-frontend/CONVENTIONS.md`)

### Enums

- Tout type domaine a valeurs finies DOIT etre un enum Java (jamais `String`)
- Les enums sont stockes en base via `@Enumerated(EnumType.STRING)`
- Les DTOs DOIVENT utiliser le type enum directement (pas `String`)
- Les enums vivent dans `modules/{module}/enums/` ou `user/enums/`

### Transactions

- `@Transactional` sur les methodes d'ecriture (create, update, delete)
- `@Transactional(readOnly = true)` sur les methodes de lecture

### Tests

- **Objectif : 100% de coverage.** Chaque service, controller et repository doit etre teste.
- Coverage : JaCoCo (`./mvnw verify`)
- Tests unitaires service : mocker le repository
- Tests controller : `@WebMvcTest`
- Tests repository : `@DataJpaTest`

### Verification apres modification

Apres toute modification de code, verifier **avant de commit** :

1. **Compilation** : `./mvnw clean compile`
2. **Tests** : `./mvnw test`
3. **Build complet** : `./mvnw clean package`

Ne jamais commit du code qui ne compile pas ou qui casse des tests existants.

### Tests manuels

- Fichiers `.http` dans `http-requests/{module}/{entity}.http` pour tester les endpoints avec IntelliJ / VS Code REST Client
- Chaque module doit avoir son fichier `.http`

---

## Conventions Core

Quand on modifie les packages hors-modules (auth, config, security, exception, user).

### SecurityConfig

- Endpoints publics : declarer dans `SecurityConfig.java` avec `.requestMatchers(...).permitAll()`
- Tout le reste : `anyRequest().authenticated()`
- Pattern : JWT filter avant `UsernamePasswordAuthenticationFilter`
- CSRF desactive (API stateless)
- `SessionCreationPolicy.STATELESS`

### GlobalExceptionHandler

- Reponses d'erreur standardisees (jamais de stack trace cote client)
- Ajouter un `@ExceptionHandler` pour chaque nouveau type d'exception custom

### Auth

- `Authentication auth` dans les controllers proteges
- `auth.getName()` retourne l'email de l'utilisateur authentifie
- Le service utilise cet email pour valider l'ownership

---

## Conventions Module

Quand on cree ou modifie un module dans `modules/{nom_module}/`.

### Structure obligatoire

```
modules/{nom_module}/
  model/          -> Au moins une entite JPA
  dto/            -> DTO principal + CreateRequest + UpdateRequest
  repository/     -> Interface JpaRepository
  mapper/         -> Conversion entity <-> DTO
  service/        -> @Service avec logique metier
  controller/     -> @RestController avec @RequestMapping("/api/{module}")
```

---

### Type d'architecture : Science / Identification

Modules qui utilisent le systeme d'identification par scoring. Heritent des abstractions de `modules/common/`.

**Abstractions a etendre :**
- `AbstractIdentificationService<Entity, DTO, Repo>` — scoring, recherche, identification
- `AbstractIdentificationRepository<Entity, DTO>` — matching par reflexion via `@IdentificationCriterion`
- `AbstractIdentificationController<DTO, Service>` — fournit automatiquement les endpoints

**Endpoints fournis automatiquement :**
- `GET /api/{module}` — lister tout
- `GET /api/{module}/{id}` — detail par ID
- `GET /api/{module}/search?query=` — recherche textuelle
- `GET /api/{module}/identify/api/{code}` — identification par code API
- `POST /api/{module}/identify` — identification par criteres (scoring)

**DTO :**
- Annoter les champs de scoring avec `@IdentificationCriterion`

**Repository : 3 fichiers :**
1. `{Entity}RepositoryCustom` extends `IdentificationRepositoryCustom<Entity, DTO>`
2. `{Entity}RepositoryImpl` extends `AbstractIdentificationRepository<Entity, DTO>`
3. `{Entity}Repository` extends `JpaRepository<Entity, Long>, {Entity}RepositoryCustom`

**Service :**
- Extends `AbstractIdentificationService<Entity, DTO, Repo>`
- Implementer : `toDTO()`, `toDTO(entity, score)`, `getEntityName()`, `findBestMatches()`, `findByApiCode()`, `findBySpeciesContaining()`

**Controller :**
- Extends `AbstractIdentificationController<DTO, Service>`
- Override les methodes uniquement pour ajouter `@Operation` (Swagger)

**Reference** : `modules/bacteriology/`

---

### Type d'architecture : CRUD owned (contacts, notes, quickshare)

Modules ou chaque entite appartient a un utilisateur. Ownership verifie a chaque operation.

**Entite :**
- `@ManyToOne(fetch = FetchType.LAZY)` vers `User` (owner)
- Champs temporels : `createdAt`, `updatedAt`
- `@Builder`, `@Data`, `@Entity`, `@NoArgsConstructor`, `@AllArgsConstructor`

**DTOs :**
- `{Entity}DTO` — reponse API
- `Create{Entity}Request` — creation
- `Update{Entity}Request` — modification (champs nullable pour partial update)

**Service :**
- `@Service`, `@RequiredArgsConstructor`, `@Slf4j`
- Chaque methode recoit `String userEmail` (depuis `auth.getName()`)
- Lookup user : `userRepository.findByEmail(userEmail)`
- **Verification ownership** : comparer `entity.getOwner().getId()` avec l'user connecte
- `@Transactional` pour les ecritures, `@Transactional(readOnly = true)` pour les lectures

**Controller :**
- `@RestController`, `@RequestMapping("/api/{module}")`
- `@Tag(name = "...", description = "...")`
- Endpoints standard :

| Methode | Path | Description |
|---------|------|-------------|
| `POST` | `/` | Creer |
| `GET` | `/` | Lister (filtre par owner) |
| `GET` | `/{id}` | Detail par ID |
| `PUT` | `/{id}` | Mettre a jour |
| `DELETE` | `/{id}` | Supprimer (204) |
| `GET` | `/search?query=` | Rechercher |

Endpoints optionnels :
| `PATCH` | `/{id}/favorite` | Toggle favori |
| `PATCH` | `/{id}/pin` | Toggle epingle |

**Reference** : `modules/contacts/`

---

### Type d'architecture : Systeme (notifications, support, catalog)

Modules avec une logique specifique qui ne rentre pas dans les patterns ci-dessus. Structure libre, mais respecter les conventions generales (Lombok, validation, Swagger, etc.).

**Reference** : `modules/notifications/`

> **Note** : Ces trois types d'architecture couvrent les besoins actuels. D'autres types peuvent etre crees si un nouveau module ne correspond a aucun pattern existant — documenter le nouveau type dans cette section.

---

## Catalogue des modules (`modules/catalog`)

Le catalogue backend gere la **logique metier** des modules : prix, acces utilisateur, verrouillage. Les metadonnees d'affichage (icon, route, titre, description) sont gerees par le **frontend registry** (`ModuleDefinition`). Le `module_key` dans `data.sql` doit correspondre au `moduleKey` dans le `definition.ts` frontend.

Quand on ajoute un module au catalogue (`data.sql`), les champs `icon` et `route_path` sont des fallbacks pour les modules pas encore implementes cote frontend. Pour les modules implementes, le frontend les ignore et utilise le registry.

---

## Guide de creation d'un module

> **IMPORTANT** : Consulter le **[Guide complet de creation de module](../OLS-documentation/module-creation-guide.md)** dans le repo `OLS-documentation`. Ce guide contient le tutoriel step-by-step backend + frontend, les exemples de code complets, le catalogue exhaustif des ressources plateforme, et les checklists. C'est la reference unique pour creer un nouveau module — le lire en entier avant de commencer.

---

## Communication inter-modules

Les modules communiquent entre eux via deux mecanismes dans `modules/common/` :

| Besoin | Mecanisme | Package |
|--------|-----------|---------|
| "Il s'est passe quelque chose" (side-effect) | **Domain Event** (fire-and-forget, 0..N listeners) | `common/event/` |
| "J'ai besoin de donnees d'un autre module" (requete) | **SPI interface** (synchrone, 1:1) | `common/spi/` |

### Domain Events

- Les events sont des **records Java** implementant `ModuleEvent`
- Les events vivent dans `modules/common/event/` (jamais dans le module source)
- Publier via `ApplicationEventPublisher.publishEvent(...)`
- Ecouter via `@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)`
- Les listeners vivent dans `modules/{module_cible}/listener/`
- Toujours wrapper le corps du listener dans un try-catch + `log.warn`

```java
// Publier (dans le service source)
eventPublisher.publishEvent(new ShareCreatedEvent(...));

// Ecouter (dans le module cible)
@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
public void onShareCreated(ShareCreatedEvent event) { ... }
```

**Events existants :**

| Domain Event | NotificationType | Listeners |
|---|---|---|
| `ShareCreatedEvent` | `QUICKSHARE_RECEIVED` | notifications, contacts |
| `TicketRepliedEvent` | `SUPPORT_REPLY` | notifications |
| `TicketStatusChangedEvent` | `SUPPORT_STATUS_CHANGED` | notifications |
| `ModuleAccessGrantedEvent` | `MODULE_ACCESS_GRANTED` | notifications |
| `MemberAddedEvent` | `ORGANIZATION_INVITED` | notifications |
| `MemberRemovedEvent` | `ORGANIZATION_REMOVED` | notifications |
| `MemberRoleChangedEvent` | `ORGANIZATION_ROLE_CHANGED` | notifications |

### Notifications temps reel (SSE)

`NotificationService.send()` persiste la notification en BDD puis la pousse en temps reel via `SseEmitterService`. Le frontend se connecte a `GET /api/notifications/stream` (SSE) et invalide automatiquement le cache TanStack Query a chaque notification recue.

Ajouter un nouveau type de notification :
1. Ajouter la valeur dans `NotificationType.java` (backend)
2. Creer le Domain Event dans `common/event/`
3. Ajouter le listener dans `NotificationEventListener`
4. Frontend : `notification-rendering.tsx` (icone/couleur), `types.ts` (label), i18n (`fr.json`, `en.json`)

### SPI (Service Provider Interface)

- L'interface vit dans `modules/common/spi/`
- L'implementation reste dans le module concerne (`@Service` classique)
- Le consommateur injecte l'interface, Spring autowire l'implementation

```java
// Interface dans common/spi/
public interface InventoryQuerySPI {
    List<LowStockItem> getLowStockItems(String userEmail);
}

// Implementation dans le module inventaire
@Service
public class InventoryService implements InventoryQuerySPI { ... }

// Consommateur dans un autre module
private final InventoryQuerySPI inventoryQuery;
```

**SPI existants :**

| SPI | Implemente par | Usage |
|-----|----------------|-------|
| `UserQuerySPI` | `UserService` | Lookup utilisateur (`findByEmail`, `findById`, `existsByEmail`, `search`) — remplace l'injection directe de `UserRepository` dans les modules |
| `OrganizationQuerySPI` | `OrganizationQueryService` | Requetes organisation/membership (`isUserMemberOf`, `getUserRoleInOrg`, `isSupervisorOf`) |

### Regle

- **Jamais** d'import direct d'un service ou repository d'un autre module
- **Jamais** d'import de `UserRepository` dans un module — utiliser `UserQuerySPI`
- Un module peut importer uniquement `modules/common/event/`, `modules/common/spi/`, et `modules/common/model/`
- Tout couplage inter-module passe par un event ou un SPI

---

## Conventions de nommage

| Element | Convention | Exemple |
|---------|------------|---------|
| Package | `modules.{module}` | `modules.contacts` |
| Entite | PascalCase, singulier | `Contact`, `Bacterium` |
| DTO | PascalCase + `DTO` ou `Request` | `ContactDTO`, `CreateContactRequest` |
| Service | PascalCase + `Service` | `ContactService` |
| Controller | PascalCase + `Controller` | `ContactController` |
| Repository | PascalCase + `Repository` | `ContactRepository` |
| Mapper | PascalCase + `Mapper` | `ContactMapper` |
| Endpoint | kebab-case | `/api/contacts/{id}/favorite` |
| Table | snake_case, prefixe si module complexe | `contacts`, `chat_servers` |

---

## Securite

- JWT (access + refresh tokens) via JJWT 0.12.6
- Filtre : `JwtAuthenticationFilter` dans la chaine Spring Security
- Support guest tokens (utilisateurs anonymes)
- Rate limiting : Bucket4j (10k req/min dev, 60 req/min prod)
- CORS : localhost:3000 (dev), odinlascience.com (prod)

---

## Profils

- `dev` : `ddl-auto=create-drop`, SQL verbose, `data.sql` charge automatiquement, rate limits permissifs
- `prod` : `ddl-auto=validate`, config par variables d'environnement

