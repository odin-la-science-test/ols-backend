package com.odinlascience.backend.modules.ai.controller;

import com.odinlascience.backend.modules.ai.dto.CorrectionRequest;
import com.odinlascience.backend.modules.ai.dto.CorrectionResponse;
import com.odinlascience.backend.modules.ai.service.TextCorrectionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ai")
@Tag(name = "AI", description = "Correction de texte et outils IA")
@RequiredArgsConstructor
public class TextCorrectionController {

    private final TextCorrectionService correctionService;

    @PostMapping("/correct")
    @Operation(summary = "Corriger un texte",
               description = "Retourne les corrections orthographiques et grammaticales pour le texte fourni")
    public ResponseEntity<CorrectionResponse> correct(
            @Valid @RequestBody CorrectionRequest request,
            Authentication auth
    ) {
        String language = request.getLanguage() != null ? request.getLanguage() : "fr";
        return ResponseEntity.ok(correctionService.correct(request.getText(), language));
    }
}
