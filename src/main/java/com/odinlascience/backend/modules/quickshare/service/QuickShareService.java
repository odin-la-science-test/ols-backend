package com.odinlascience.backend.modules.quickshare.service;

import com.odinlascience.backend.exception.ResourceNotFoundException;
import com.odinlascience.backend.modules.common.event.ShareCreatedEvent;
import com.odinlascience.backend.modules.common.service.UserHelper;
import com.odinlascience.backend.modules.quickshare.dto.CreateTextShareRequest;
import com.odinlascience.backend.modules.quickshare.dto.SharedItemDTO;
import com.odinlascience.backend.modules.quickshare.enums.ShareType;
import com.odinlascience.backend.modules.quickshare.mapper.SharedItemMapper;
import com.odinlascience.backend.modules.quickshare.model.SharedItem;
import com.odinlascience.backend.modules.quickshare.repository.SharedItemRepository;
import com.odinlascience.backend.user.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.List;

/**
 * Service principal de QuickShare.
 * Gere le partage de texte et de fichiers avec codes de partage uniques.
 * Les operations fichier sont deleguees a {@link QuickShareFileService}.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class QuickShareService {

    private final SharedItemRepository repository;
    private final SharedItemMapper mapper;
    private final UserHelper userHelper;
    private final ApplicationEventPublisher eventPublisher;
    private final QuickShareFileService fileService;

    private static final String SHARE_CODE_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int SHARE_CODE_LENGTH = 8;
    private static final SecureRandom RANDOM = new SecureRandom();

    // ─── Créer un partage de texte ───

    @Transactional
    public SharedItemDTO createTextShare(CreateTextShareRequest request, String userEmail) {
        User owner = userHelper.findByEmail(userEmail);

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

        publishShareEvent(request.getRecipientEmail(), owner, saved);
        return mapper.toDTO(saved);
    }

    // ─── Créer un partage de fichier(s) ───

    @Transactional
    public SharedItemDTO createFileShare(
            MultipartFile[] files, String title, Integer maxDownloads,
            Instant expiresAt, String recipientEmail, String userEmail
    ) throws IOException {
        User owner = userHelper.findByEmail(userEmail);

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
        fileService.storeFiles(files, saved);
        saved = repository.save(saved);

        log.info("File share created: code={}, fileCount={}, owner={}",
                saved.getShareCode(), saved.getFiles().size(), userEmail);

        publishShareEvent(recipientEmail, owner, saved);
        return mapper.toDTO(saved);
    }

    // ─── Consultation publique par code ───

    @Transactional(readOnly = true)
    public SharedItemDTO getByShareCode(String shareCode) {
        SharedItem item = repository.findByShareCode(shareCode)
                .orElseThrow(() -> new ResourceNotFoundException("Aucun partage trouvé avec le code : " + shareCode));
        return mapper.toDTO(item);
    }

    // ─── Enregistrer une consultation ───

    @Transactional
    public void recordView(String shareCode) {
        SharedItem item = repository.findByShareCode(shareCode)
                .orElseThrow(() -> new ResourceNotFoundException("Aucun partage trouvé avec le code : " + shareCode));

        if (item.isExpired() || item.isDownloadLimitReached()) return;

        item.incrementDownloadCount();
        repository.save(item);
    }

    // ─── Recherche & listing ───

    @Transactional(readOnly = true)
    public List<SharedItemDTO> search(String query, String userEmail) {
        User owner = userHelper.findByEmail(userEmail);
        return repository.searchByOwner(owner.getId(), query).stream().map(mapper::toDTO).toList();
    }

    @Transactional(readOnly = true)
    public List<SharedItemDTO> getMyShares(String userEmail) {
        User owner = userHelper.findByEmail(userEmail);
        return repository.findByOwnerIdOrderByCreatedAtDesc(owner.getId()).stream().map(mapper::toDTO).toList();
    }

    @Transactional(readOnly = true)
    public SharedItemDTO getById(Long id, String userEmail) {
        SharedItem item = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Partage introuvable avec l'ID : " + id));
        userHelper.verifyOwnership(item, userEmail, "Partage", id);
        return mapper.toDTO(item);
    }

    // ─── Supprimer un partage ───

    @Transactional
    public void delete(Long id, String userEmail) {
        SharedItem item = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Partage introuvable avec l'ID : " + id));
        userHelper.verifyOwnership(item, userEmail, "Partage", id);

        fileService.deleteFiles(item);
        repository.delete(item);
        log.info("Share deleted: id={}, code={}, owner={}", id, item.getShareCode(), userEmail);
    }

    // ─── Helpers ───

    private String generateShareCode() {
        StringBuilder sb = new StringBuilder(SHARE_CODE_LENGTH);
        for (int i = 0; i < SHARE_CODE_LENGTH; i++) {
            sb.append(SHARE_CODE_CHARS.charAt(RANDOM.nextInt(SHARE_CODE_CHARS.length())));
        }
        String code = sb.toString();
        if (repository.findByShareCode(code).isPresent()) {
            return generateShareCode();
        }
        return code;
    }

    private void publishShareEvent(String recipientEmail, User sender, SharedItem item) {
        if (recipientEmail == null || recipientEmail.isBlank()) return;
        if (recipientEmail.equalsIgnoreCase(sender.getEmail())) return;

        String senderName = sender.getFullName();
        if (senderName == null || senderName.isBlank()) senderName = sender.getEmail();

        eventPublisher.publishEvent(new ShareCreatedEvent(
                item.getShareCode(),
                item.getTitle(),
                sender.getEmail(),
                senderName.trim(),
                recipientEmail,
                "/lab/quickshare"
        ));
    }
}
