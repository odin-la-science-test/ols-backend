package com.odinlascience.backend.modules.mycology.controller;

import com.odinlascience.backend.modules.mycology.dto.FungusDTO;
import com.odinlascience.backend.modules.mycology.service.FungusService;
import com.odinlascience.backend.modules.common.controller.AbstractIdentificationController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/fungi") 
@Tag(name = "Champignons", description = "API de gestion des données mycologiques")
public class FungusController extends AbstractIdentificationController<FungusDTO, FungusService> {

    public FungusController(FungusService service) {
        super(service);
    }

    @Override
    @Operation(summary = "Lister tous les champignons", description = "Retourne la liste complète des champignons disponibles dans la base de données sous forme de DTO.")
    public org.springframework.http.ResponseEntity<java.util.List<FungusDTO>> getAll() {
        return super.getAll();
    }

    @Override
    @Operation(summary = "Récupérer un champignon par son ID", description = "Récupère les détails d'un champignon identifié par son identifiant numérique unique.")
    public org.springframework.http.ResponseEntity<FungusDTO> getById(@PathVariable Long id) {
        return super.getById(id);
    }

    @Override
    @Operation(summary = "Rechercher par espèce (ex: 'Saccharomyces')", description = "Recherche et renvoie les champignons dont le nom d'espèce contient la chaîne fournie en paramètre 'query'.")
    public org.springframework.http.ResponseEntity<java.util.List<FungusDTO>> searchBySpecies(@RequestParam String query) {
        return super.searchBySpecies(query);
    }

    @Override
    @Operation(summary = "Identifier via code API (ex: SAC001)", description = "Retourne le champignon correspondant au code externe fourni via l'API d'identification.")
    public org.springframework.http.ResponseEntity<FungusDTO> identifyByApiCode(@PathVariable String code) {
        return super.identifyByApiCode(code);
    }

    @Override
    @Operation(summary = "Identifier par profil mycologique", description = "Envoie un profil (Type, Catégorie, Aérobie...) et reçoit les meilleures correspondances avec score.")
    public org.springframework.http.ResponseEntity<java.util.List<FungusDTO>> identifyByCriteria(@RequestBody FungusDTO criteria) {
        return super.identifyByCriteria(criteria);
    }
}