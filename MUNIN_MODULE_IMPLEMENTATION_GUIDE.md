# Guide d'Implémentation d'un Nouveau Module Munin Atlas

Ce guide décrit la procédure standardisée pour créer un nouveau module dans le "Munin Atlas" de l'application OLS-backend (ex: Virologie, Parasitologie, etc.).

L'architecture suit une approche **DRY (Don't Repeat Yourself)** stricte en utilisant des classes génériques situées dans `com.odinlascience.backend.modules.common`.

## 1. Architecture du Module

Chaque module doit respecter la structure de paquets suivante sous `com.odinlascience.backend.modules.<nom_module>` :

*   `model/` : Entités JPA.
*   `dto/` : Objets de transfert de données.
*   `repository/` : Interfaces et implémentations d'accès aux données.
*   `service/` : Logique métier.
*   `controller/` : Points d'entrée API REST.
*   `mapper/` : Conversion Entity <-> DTO.
*   `enums/` : Énumérations spécifiques au domaine.

## 2. Étapes d'Implémentation (De A à Z)

Prenons l'exemple d'un module **Virology** (Virologie) où l'entité principale est `Virus`.

### Étape 0 : Analyse du Prototype (CRUCIAL)

Avant tout code, **analysez le fichier HTML du prototype** correspondant (ex: `prototype/virology.html`).
1.  Listez tous les champs de saisie (inputs, selets, checkboxes) utilisés pour l'identification.
2.  Listez toutes les colonnes de données affichées dans les tableaux de résultats.
3.  **Ces champs doivent impérativement exister dans le Modèle et le DTO.**

### Étape 1 : Créer le Modèle (`model/Virus.java`)

Créer l'entité JPA standard. Elle n'a pas besoin d'hériter d'une classe commune, mais doit avoir un ID.

```java
@Entity
@Table(name = "viruses")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Virus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String species;
    private String apiCode;
    // ... autres champs spécifiques
}
```

### Étape 2 : Créer le DTO (`dto/VirusDTO.java`)

Créer le DTO. **Important** :
1.  Incluez tous les champs identifiés à l'**Étape 0**.
2.  Annotez les champs qui doivent être utilisés pour l'algorithme d'identification (scoring) avec `@IdentificationCriterion`.

```java
@Data
@Builder
// ...
public class VirusDTO {
    private Long id;
    private String species;

    @IdentificationCriterion // Ce champ sera utilisé pour le scoring (match exact = points)
    private Boolean isRna;

    @IdentificationCriterion
    private String capsuleType;
}
```

### Étape 3 : Créer le Repository (`repository/`)

Il faut 3 fichiers pour le repository afin de supporter la recherche générique et l'injection de dépendances Spring Data.

**A. Interface Custom (`VirusRepositoryCustom.java`)**
Doit étendre `IdentificationRepositoryCustom`.

```java
public interface VirusRepositoryCustom extends IdentificationRepositoryCustom<Virus, VirusDTO> {
}
```

**B. Implémentation Custom (`VirusRepositoryImpl.java`)**
Doit étendre `AbstractIdentificationRepository`. C'est ici que l'identification par réflexion est activée.

```java
@Repository
public class VirusRepositoryImpl extends AbstractIdentificationRepository<Virus, VirusDTO> {
    @Override
    protected Class<Virus> getEntityClass() { return Virus.class; }

    @Override
    protected Class<VirusDTO> getDtoClass() { return VirusDTO.class; }
}
```

**C. Interface Principale (`VirusRepository.java`)**
Etend `JpaRepository` et l'interface custom.

```java
@Repository
public interface VirusRepository extends JpaRepository<Virus, Long>, VirusRepositoryCustom {
    // Méthodes requises par le service abstrait pour la recherche textuelle et par code
    List<Virus> findBySpeciesContainingIgnoreCase(String species);
    Optional<Virus> findByApiCode(String apiCode);
}
```

### Étape 4 : Créer le Mapper (`mapper/VirusMapper.java`)

Créer un composant pour mapper entre Entity et DTO. Utiliser MapStruct ou du code manuel.

```java
@Component
public class VirusMapper {
    public VirusDTO toDTO(Virus entity) { /* ... */ }
    
    // Surcharge pour inclure le score lors d'une identification
    public VirusDTO toDTO(Virus entity, Integer score) { 
        VirusDTO dto = toDTO(entity);
        // Ajouter le score au DTO si un champ score existe, ou le wrapper
        return dto; 
    }
}
```

### Étape 5 : Créer le Service (`service/VirusService.java`)

Doit étendre `AbstractIdentificationService`. C'est le cœur de la logique DRY.

```java
@Service
public class VirusService extends AbstractIdentificationService<Virus, VirusDTO, VirusRepository> {
    private final VirusMapper mapper;

    public VirusService(VirusRepository repository, VirusMapper mapper) {
        super(repository);
        this.mapper = mapper;
    }

    @Override
    protected VirusDTO toDTO(Virus entity) { return mapper.toDTO(entity); }

    @Override
    protected VirusDTO toDTO(Virus entity, Integer score) { return mapper.toDTO(entity, score); }

    @Override
    protected String getEntityName() { return "Virus"; }

    @Override
    protected List<IdentifiableMatch<Virus>> findBestMatches(VirusDTO criteria, int limit) {
        return repository.findBestMatches(criteria, limit);
    }
    
    @Override
    protected Optional<Virus> findByApiCode(String apiCode) {
        return repository.findByApiCode(apiCode);
    }

    @Override
    protected List<Virus> findBySpeciesContaining(String query) {
        return repository.findBySpeciesContainingIgnoreCase(query);
    }
}
```

### Étape 6 : Créer le Controller (`controller/VirusController.java`)

Doit étendre `AbstractIdentificationController`. Cela fournit automatiquement les endpoints `/search`, `/identify`, `/identify/api/{code}`, etc.

```java
@RestController
@RequestMapping("/api/viruses")
@Tag(name = "Virologie")
public class VirusController extends AbstractIdentificationController<VirusDTO, VirusService> {
    
    public VirusController(VirusService service) {
        super(service);
    }

    // Surcharger TOUTES les méthodes publiques (getAll, getById, etc.) uniquement pour ajouter 
    // les annotations Swagger (@Operation) et appeler super.method().
    
    @Override
    @Operation(summary = "Identifier par critères")
    public ResponseEntity<List<VirusDTO>> identifyByCriteria(@RequestBody VirusDTO criteria) {
        return super.identifyByCriteria(criteria);
    }
}
```

## 3. Tests et Requêtes HTTP

### Tests Unitaires
Structure à reproduire dans `src/test/java/com/odinlascience/backend/modules/<module>` :
1.  **Test Service** : Mocker le repository et vérifier que `identifyByCriteria` appelle `findBestMatches`.
2.  **Test Controller** : `WebMvcTest` sur le contrôleur.
3.  **Test Repository** : `DataJpaTest` pour vérifier que `findBestMatches` fonctionne avec l'annotation `@IdentificationCriterion`.

### Fichier HTTP
Créer un fichier `.http` dans `http-requests/<module>/<entity>.http` pour tester manuellement les endpoints.

Exemple :
```http
### Configuration (Dev)
@host = http://localhost:8080
@auth = {{host}}/api/auth
@api = {{host}}/api/viruses

### Login
# ...

### Identifier
POST {{api}}/identify
Content-Type: application/json
Authorization: Bearer {{token}}

{
  "isRna": true,
  "capsuleType": "HELICAL"
}
```
