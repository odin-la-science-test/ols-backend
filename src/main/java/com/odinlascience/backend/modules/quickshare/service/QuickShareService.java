package com.odinlascience.backend.modules.quickshare.service;

import com.odinlascience.backend.exception.ResourceNotFoundException;
import com.odinlascience.backend.modules.contacts.service.ContactService;
import com.odinlascience.backend.modules.notifications.enums.NotificationType;
import com.odinlascience.backend.modules.notifications.service.NotificationService;
import com.odinlascience.backend.modules.quickshare.dto.CreateTextShareRequest;
import com.odinlascience.backend.modules.quickshare.dto.SharedItemDTO;
import com.odinlascience.backend.modules.quickshare.enums.ShareType;
import com.odinlascience.backend.modules.quickshare.mapper.SharedItemMapper;
import com.odinlascience.backend.modules.quickshare.model.SharedFile;
import com.odinlascience.backend.modules.quickshare.model.SharedItem;
import com.odinlascience.backend.modules.quickshare.repository.SharedFileRepository;
import com.odinlascience.backend.modules.quickshare.repository.SharedItemRepository;
import com.odinlascience.backend.user.model.User;
import com.odinlascience.backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Service principal de QuickShare.
 * Gère le partage de texte et de fichiers avec codes de partage uniques.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class QuickShareService {

    private final SharedItemRepository repository;
    private final SharedFileRepository fileRepository;
    private final SharedItemMapper mapper;
    private final UserRepository userRepository;
    private final NotificationService notificationService;
    private final ContactService contactService;

    @Value("${quickshare.upload-dir:./uploads/quickshare}")
    private String uploadDir;

    @Value("${quickshare.max-file-size:52428800}")
    private long maxFileSize; // 50 MB par défaut

    private static final String SHARE_CODE_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int SHARE_CODE_LENGTH = 8;
    private static final SecureRandom RANDOM = new SecureRandom();

    // ─── Créer un partage de texte ───

    @Transactional
    public SharedItemDTO createTextShare(CreateTextShareRequest request, String userEmail) {
        User owner = findUserByEmail(userEmail);

        SharedItem item = SharedItem.builder()
                .shareCode(generateShareCode())
                .title(request.getTitle())
                .type(ShareType.TEXT)
                .textContent(request.getTextContent())
                .maxDownloads(request.getMaxDownloads())
                .expiresAt(request.getExpiresAt())
                .createdAt(Instant.now())
                .owner(owner)
                .recipientEmail(request.getRecipientEmail())
                .downloadCount(0)
                .build();

        SharedItem saved = repository.save(item);
        log.info("Text share created: code={}, owner={}", saved.getShareCode(), userEmail);

        // Notification directe au destinataire si spécifié
        sendShareNotification(request.getRecipientEmail(), owner, saved);

        return mapper.toDTO(saved);
    }

    // ─── Créer un partage de fichier(s) ───

    @Transactional
    public SharedItemDTO createFileShare(
            MultipartFile[] files,
            String title,
            Integer maxDownloads,
            Instant expiresAt,
            String recipientEmail,
            String userEmail
    ) throws IOException {
        User owner = findUserByEmail(userEmail);

        if (files == null || files.length == 0) {
            throw new IllegalArgumentException("Aucun fichier fourni");
        }

        // Créer le dossier d'upload si nécessaire
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        SharedItem item = SharedItem.builder()
                .shareCode(generateShareCode())
                .title(title)
                .type(ShareType.FILE)
                .maxDownloads(maxDownloads)
                .expiresAt(expiresAt)
                .createdAt(Instant.now())
                .owner(owner)
                .recipientEmail(recipientEmail)
                .downloadCount(0)
                .build();

        SharedItem saved = repository.save(item);

        // Sauvegarder chaque fichier
        for (MultipartFile file : files) {
            if (file.isEmpty()) continue;
            if (file.getSize() > maxFileSize) {
                throw new IllegalArgumentException("Le fichier '" + file.getOriginalFilename() + "' dépasse la taille maximale autorisée");
            }

            String storedFilename = UUID.randomUUID().toString();
            String originalFilename = file.getOriginalFilename();
            if (originalFilename != null && originalFilename.contains(".")) {
                String ext = originalFilename.substring(originalFilename.lastIndexOf("."));
                storedFilename += ext;
            }

            Path targetPath = uploadPath.resolve(storedFilename);
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

            SharedFile sharedFile = SharedFile.builder()
                    .originalFilename(originalFilename)
                    .storedFilename(storedFilename)
                    .contentType(file.getContentType())
                    .fileSize(file.getSize())
                    .sharedItem(saved)
                    .build();

            saved.getFiles().add(sharedFile);
        }

        saved = repository.save(saved);
        log.info("File share created: code={}, fileCount={}, owner={}", saved.getShareCode(), saved.getFiles().size(), userEmail);

        // Notification directe au destinataire si spécifié
        sendShareNotification(recipientEmail, owner, saved);

        return mapper.toDTO(saved);
    }

    // ─── Récupérer par code de partage (consultation publique) ───

    @Transactional(readOnly = true)
    public SharedItemDTO getByShareCode(String shareCode) {
        SharedItem item = repository.findByShareCode(shareCode)
                .orElseThrow(() -> new ResourceNotFoundException("Aucun partage trouvé avec le code : " + shareCode));

        return mapper.toDTO(item);
    }

    // ─── Enregistrer une consultation (incrément unique) ───

    @Transactional
    public void recordView(String shareCode) {
        SharedItem item = repository.findByShareCode(shareCode)
                .orElseThrow(() -> new ResourceNotFoundException("Aucun partage trouvé avec le code : " + shareCode));

        if (item.isExpired() || item.isDownloadLimitReached()) {
            return; // Silently ignore
        }

        item.incrementDownloadCount();
        repository.save(item);
    }

    // ─── Télécharger un fichier spécifique par fileId ───

    @Transactional(readOnly = true)
    public Resource downloadFile(String shareCode, Long fileId) {
        SharedItem item = repository.findByShareCode(shareCode)
                .orElseThrow(() -> new ResourceNotFoundException("Aucun partage trouvé avec le code : " + shareCode));

        if (item.getType() != ShareType.FILE) {
            throw new IllegalArgumentException("Ce partage n'est pas un fichier");
        }
        if (item.isExpired()) {
            throw new ResourceNotFoundException("Ce partage a expiré");
        }
        if (item.isDownloadLimitReached()) {
            throw new ResourceNotFoundException("Ce partage a atteint sa limite de téléchargements");
        }

        SharedFile sharedFile = item.getFiles().stream()
                .filter(f -> f.getId().equals(fileId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Fichier introuvable avec l'ID : " + fileId));

        try {
            Path filePath = Paths.get(uploadDir).resolve(sharedFile.getStoredFilename()).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if (!resource.exists()) {
                throw new ResourceNotFoundException("Fichier introuvable sur le serveur");
            }
            return resource;
        } catch (MalformedURLException e) {
            throw new ResourceNotFoundException("Fichier introuvable sur le serveur");
        }
    }

    /** Récupérer un SharedFile par son ID (pour headers de téléchargement) */
    public SharedFile getSharedFile(Long fileId) {
        return fileRepository.findById(fileId)
                .orElseThrow(() -> new ResourceNotFoundException("Fichier introuvable"));
    }

    // ─── Télécharger tous les fichiers d'un partage en ZIP ───

    @Transactional(readOnly = true)
    public void writeZip(String shareCode, java.io.OutputStream outputStream) throws IOException {
        SharedItem item = repository.findByShareCode(shareCode)
                .orElseThrow(() -> new ResourceNotFoundException("Aucun partage trouvé avec le code : " + shareCode));

        if (item.getType() != ShareType.FILE) {
            throw new IllegalArgumentException("Ce partage n'est pas un fichier");
        }
        if (item.isExpired()) {
            throw new ResourceNotFoundException("Ce partage a expiré");
        }
        if (item.isDownloadLimitReached()) {
            throw new ResourceNotFoundException("Ce partage a atteint sa limite de téléchargements");
        }

        Path uploadPath = Paths.get(uploadDir);

        try (java.util.zip.ZipOutputStream zos = new java.util.zip.ZipOutputStream(outputStream)) {
            for (SharedFile file : item.getFiles()) {
                Path filePath = uploadPath.resolve(file.getStoredFilename()).normalize();
                if (!Files.exists(filePath)) {
                    log.warn("Fichier physique introuvable pour le ZIP: {}", file.getStoredFilename());
                    continue;
                }
                zos.putNextEntry(new java.util.zip.ZipEntry(file.getOriginalFilename()));
                Files.copy(filePath, zos);
                zos.closeEntry();
            }
        }
    }

    /** Nom du ZIP basé sur le titre ou le code */
    @Transactional(readOnly = true)
    public String getZipFilename(String shareCode) {
        SharedItem item = repository.findByShareCode(shareCode)
                .orElseThrow(() -> new ResourceNotFoundException("Aucun partage trouvé"));
        String base = item.getTitle() != null && !item.getTitle().isBlank()
                ? item.getTitle().replaceAll("[^a-zA-Z0-9À-ÿ _-]", "")
                : "quickshare-" + shareCode;
        return base + ".zip";
    }

    // ─── Mes partages (utilisateur connecté) ───

    @Transactional(readOnly = true)
    public List<SharedItemDTO> getMyShares(String userEmail) {
        User owner = findUserByEmail(userEmail);
        return repository.findByOwnerIdOrderByCreatedAtDesc(owner.getId())
                .stream()
                .map(mapper::toDTO)
                .toList();
    }

    // ─── Détail d'un partage par ID (owner only) ───

    @Transactional(readOnly = true)
    public SharedItemDTO getById(Long id, String userEmail) {
        SharedItem item = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Partage introuvable avec l'ID : " + id));

        User owner = findUserByEmail(userEmail);
        if (!item.getOwner().getId().equals(owner.getId())) {
            throw new ResourceNotFoundException("Partage introuvable avec l'ID : " + id);
        }

        return mapper.toDTO(item);
    }

    // ─── Supprimer un partage ───

    @Transactional
    public void delete(Long id, String userEmail) {
        SharedItem item = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Partage introuvable avec l'ID : " + id));

        User owner = findUserByEmail(userEmail);
        if (!item.getOwner().getId().equals(owner.getId())) {
            throw new ResourceNotFoundException("Partage introuvable avec l'ID : " + id);
        }

        // Supprimer tous les fichiers physiques
        if (item.getType() == ShareType.FILE && item.getFiles() != null) {
            for (SharedFile file : item.getFiles()) {
                try {
                    Path filePath = Paths.get(uploadDir).resolve(file.getStoredFilename());
                    Files.deleteIfExists(filePath);
                } catch (IOException e) {
                    log.warn("Impossible de supprimer le fichier physique: {}", file.getStoredFilename(), e);
                }
            }
        }

        repository.delete(item);
        log.info("Share deleted: id={}, code={}, owner={}", id, item.getShareCode(), userEmail);
    }

    // ─── Helpers ───

    private User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur introuvable : " + email));
    }

    private String generateShareCode() {
        StringBuilder sb = new StringBuilder(SHARE_CODE_LENGTH);
        for (int i = 0; i < SHARE_CODE_LENGTH; i++) {
            sb.append(SHARE_CODE_CHARS.charAt(RANDOM.nextInt(SHARE_CODE_CHARS.length())));
        }
        // Vérifier l'unicité
        String code = sb.toString();
        if (repository.findByShareCode(code).isPresent()) {
            return generateShareCode(); // Récursion (extrêmement rare)
        }
        return code;
    }

    /**
     * Envoie une notification au destinataire si son email est valide et correspond à un utilisateur OLS.
     */
    private void sendShareNotification(String recipientEmail, User sender, SharedItem item) {
        if (recipientEmail == null || recipientEmail.isBlank()) return;

        // Ne pas se notifier soi-même
        if (recipientEmail.equalsIgnoreCase(sender.getEmail())) return;

        // Vérifier que le destinataire est un utilisateur OLS
        User recipient = userRepository.findByEmail(recipientEmail).orElse(null);
        if (recipient == null) {
            log.info("Share recipient {} is not an OLS user, skipping notification", recipientEmail);
            return;
        }

        // Auto-ajouter le destinataire dans les contacts de l'expéditeur
        try {
            contactService.ensureContactExists(sender.getEmail(), recipient);
        } catch (Exception e) {
            log.warn("Failed to auto-add contact for sender {}: {}", sender.getEmail(), e.getMessage());
        }

        String senderName = (sender.getFirstName() != null ? sender.getFirstName() : "")
                + (sender.getLastName() != null ? " " + sender.getLastName() : "");
        if (senderName.isBlank()) senderName = sender.getEmail();

        String title = senderName.trim() + " vous a partagé du contenu";
        String message = item.getTitle() != null && !item.getTitle().isBlank()
                ? "\"" + item.getTitle() + "\" — Code : " + item.getShareCode()
                : "Code de partage : " + item.getShareCode();

        String metadata = "{\"shareCode\":\"" + item.getShareCode() + "\",\"senderEmail\":\"" + sender.getEmail() + "\"}";

        try {
            notificationService.send(
                    recipientEmail,
                    NotificationType.QUICKSHARE_RECEIVED,
                    title,
                    message,
                    "/lab/quickshare",
                    metadata
            );
        } catch (Exception e) {
            log.warn("Failed to send share notification to {}: {}", recipientEmail, e.getMessage());
        }
    }
}
