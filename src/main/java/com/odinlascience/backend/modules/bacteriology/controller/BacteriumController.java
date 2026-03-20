package com.odinlascience.backend.modules.bacteriology.controller;

import com.odinlascience.backend.modules.bacteriology.dto.BacteriumDTO;
import com.odinlascience.backend.modules.bacteriology.service.BacteriumService;
import com.odinlascience.backend.modules.common.controller.AbstractIdentificationController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bacteria") 
@Tag(name = "Bactéries", description = "API de gestion des données bactériologiques")
public class BacteriumController extends AbstractIdentificationController<BacteriumDTO, BacteriumService> {

    public BacteriumController(BacteriumService service) {
        super(service);
    }

    @Override
    @Operation(summary = "Lister toutes les bactéries", description = "Retourne la liste complète des bactéries disponibles dans la base de données sous forme de DTO.")
    public org.springframework.http.ResponseEntity<java.util.List<BacteriumDTO>> getAll() {
        return super.getAll();
    }

    @Override
    @Operation(summary = "Récupérer une bactérie par son ID", description = "Récupère les détails d'une bactérie identifiée par son identifiant numérique unique.")
    public org.springframework.http.ResponseEntity<BacteriumDTO> getById(@PathVariable Long id) {
        return super.getById(id);
    }

    @Override
    @Operation(summary = "Rechercher par espèce (ex: 'Coli')", description = "Recherche et renvoie les bactéries dont le nom d'espèce contient la chaîne fournie en paramètre 'query'.")
    public org.springframework.http.ResponseEntity<java.util.List<BacteriumDTO>> searchBySpecies(@RequestParam String query) {
        return super.searchBySpecies(query);
    }

    @Override
    @Operation(summary = "Identifier via code API (ex: 5144572)", description = "Retourne la bactérie correspondante au code externe fourni via l'API d'identification.")
    public org.springframework.http.ResponseEntity<BacteriumDTO> identifyByApiCode(@PathVariable String code) {
        return super.identifyByApiCode(code);
    }

    @Override
    @Operation(summary = "Identifier par profil biochimique", description = "Envoie un profil (Gram, Catalase...) et reçoit les meilleures correspondances avec score.")
    public org.springframework.http.ResponseEntity<java.util.List<BacteriumDTO>> identifyByCriteria(@RequestBody BacteriumDTO criteria) {
        return super.identifyByCriteria(criteria);
    }
}