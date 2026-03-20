package com.odinlascience.backend.modules.catalog.controller;

import com.odinlascience.backend.modules.catalog.dto.AppModuleDTO;
import com.odinlascience.backend.modules.catalog.enums.ModuleType;
import com.odinlascience.backend.modules.catalog.service.AppModuleService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/modules")
@RequiredArgsConstructor
@Tag(name = "Modules", description = "Gestion du catalogue des applications (Munin & Hugin)")
public class AppModuleController {

    private final AppModuleService service;

    @GetMapping
    @Operation(summary = "Récupérer tous les modules", description = "Retourne la liste de tous les modules du catalogue sous forme de DTO.")
    public ResponseEntity<List<AppModuleDTO>> getAllModules() {
        return ResponseEntity.ok(service.getAllModules());
    }

    @GetMapping("/type/{type}")
    @Operation(summary = "Récupérer les modules par type (MUNIN_ATLAS ou HUGIN_LAB)", description = "Filtre et retourne les modules correspondant au type fourni en path variable.")
    public ResponseEntity<List<AppModuleDTO>> getModulesByType(@PathVariable ModuleType type) {
        return ResponseEntity.ok(service.getModulesByType(type));
    }

    @GetMapping("/{key}")
    @Operation(summary = "Récupérer un module spécifique par sa clé", description = "Retourne les détails d'un module identifié par sa clé unique dans le catalogue.")
    public ResponseEntity<AppModuleDTO> getModuleByKey(@PathVariable String key) {
        return ResponseEntity.ok(service.getModuleByKey(key));
    }
}