package com.odinlascience.backend.modules.quickshare.controller;

import com.odinlascience.backend.modules.quickshare.dto.CreateTextShareRequest;
import com.odinlascience.backend.modules.quickshare.dto.SharedItemDTO;
import com.odinlascience.backend.modules.quickshare.model.SharedFile;
import com.odinlascience.backend.modules.quickshare.service.QuickShareFileService;
import com.odinlascience.backend.modules.quickshare.service.QuickShareService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/quickshare")
@Tag(name = "QuickShare", description = "Partage instantané de fichiers et textes entre collaborateurs")
@RequiredArgsConstructor
public class QuickShareController {

    private final QuickShareService service;
    private final QuickShareFileService fileService;

    // ─── Créer un partage texte ───

    @PostMapping("/text")
    @Operation(summary = "Créer un partage de texte",
               description = "Partage un snippet de texte avec un code de partage unique")
    public ResponseEntity<SharedItemDTO> createTextShare(
            @Valid @RequestBody CreateTextShareRequest request,
            Authentication auth
    ) {
        SharedItemDTO dto = service.createTextShare(request, auth.getName());
        return ResponseEntity.ok(dto);
    }

    // ─── Créer un partage fichier(s) ───

    @PostMapping(value = "/file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Créer un partage de fichier(s)",
               description = "Upload un ou plusieurs fichiers et génère un code de partage unique")
    public ResponseEntity<SharedItemDTO> createFileShare(
            @RequestParam("files") MultipartFile[] files,
            @RequestParam(value = "title", required = false) String title,
            @RequestParam(value = "maxDownloads", required = false) Integer maxDownloads,
            @RequestParam(value = "expiresAt", required = false) Instant expiresAt,
            @RequestParam(value = "recipientEmail", required = false) String recipientEmail,
            Authentication auth
    ) throws IOException {
        SharedItemDTO dto = service.createFileShare(files, title, maxDownloads, expiresAt, recipientEmail, auth.getName());
        return ResponseEntity.ok(dto);
    }

    // ─── Mes partages ───

    @GetMapping
    @Operation(summary = "Lister mes partages",
               description = "Retourne tous les partages de l'utilisateur connecté")
    public ResponseEntity<List<SharedItemDTO>> getMyShares(Authentication auth) {
        return ResponseEntity.ok(service.getMyShares(auth.getName()));
    }

    // ─── Rechercher dans mes partages ───

    @GetMapping("/search")
    @Operation(summary = "Rechercher dans mes partages",
               description = "Recherche par titre, code de partage ou contenu texte")
    public ResponseEntity<List<SharedItemDTO>> search(
            @RequestParam("query") String query,
            Authentication auth
    ) {
        return ResponseEntity.ok(service.search(query, auth.getName()));
    }

    // ─── Détail d'un partage par ID ───

    @GetMapping("/{id}")
    @Operation(summary = "Détail d'un partage par ID",
               description = "Récupère les détails d'un de mes partages par son ID")
    public ResponseEntity<SharedItemDTO> getById(@PathVariable Long id, Authentication auth) {
        return ResponseEntity.ok(service.getById(id, auth.getName()));
    }

    // ─── Consulter par code de partage (lien de partage) ───

    @GetMapping("/d/{shareCode}")
    @Operation(summary = "Accéder à un partage via son code",
               description = "Récupère le contenu d'un partage via son code unique (texte ou métadonnées fichier)")
    public ResponseEntity<SharedItemDTO> getByShareCode(@PathVariable String shareCode) {
        return ResponseEntity.ok(service.getByShareCode(shareCode));
    }

    // ─── Enregistrer une consultation ───

    @PostMapping("/d/{shareCode}/view")
    @Operation(summary = "Enregistrer une consultation",
               description = "Incrémente le compteur de consultations du partage")
    public ResponseEntity<Void> recordView(@PathVariable String shareCode) {
        service.recordView(shareCode);
        return ResponseEntity.ok().build();
    }

    // ─── Télécharger un fichier spécifique d'un partage ───

    @GetMapping("/d/{shareCode}/files/{fileId}/download")
    @Operation(summary = "Télécharger un fichier spécifique",
               description = "Télécharge un fichier spécifique d'un partage via son code et l'ID du fichier")
    public ResponseEntity<Resource> downloadFile(
            @PathVariable String shareCode,
            @PathVariable Long fileId
    ) {
        Resource resource = fileService.downloadFile(shareCode, fileId);
        SharedFile sharedFile = fileService.getSharedFile(fileId);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(
                        sharedFile.getContentType() != null ? sharedFile.getContentType() : "application/octet-stream"))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + sharedFile.getOriginalFilename() + "\"")
                .body(resource);
    }

    // ─── Télécharger tous les fichiers en ZIP ───

    @GetMapping("/d/{shareCode}/download-all")
    @Operation(summary = "Télécharger tous les fichiers en ZIP",
               description = "Génère un ZIP contenant tous les fichiers d'un partage")
    public void downloadAll(
            @PathVariable String shareCode,
            jakarta.servlet.http.HttpServletResponse response
    ) throws java.io.IOException {
        String zipName = fileService.getZipFilename(shareCode);
        response.setContentType("application/zip");
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + zipName + "\"");
        fileService.writeZip(shareCode, response.getOutputStream());
        response.flushBuffer();
    }

    // ─── Supprimer un partage ───

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un partage",
               description = "Supprime un de mes partages (et le fichier physique si applicable)")
    public ResponseEntity<Void> delete(@PathVariable Long id, Authentication auth) {
        service.delete(id, auth.getName());
        return ResponseEntity.noContent().build();
    }
}
