package com.odinlascience.backend.modules.quickshare.service;

import com.odinlascience.backend.exception.ResourceNotFoundException;
import com.odinlascience.backend.modules.common.event.ShareCreatedEvent;
import com.odinlascience.backend.modules.common.service.AbstractOwnedCrudService;
import com.odinlascience.backend.modules.common.service.HtmlSanitizer;
import com.odinlascience.backend.modules.common.service.UserHelper;
import com.odinlascience.backend.modules.quickshare.dto.CreateTextShareRequest;
import com.odinlascience.backend.modules.quickshare.dto.SharedItemDTO;
import com.odinlascience.backend.modules.quickshare.enums.ShareType;
import com.odinlascience.backend.modules.quickshare.mapper.SharedItemMapper;
import com.odinlascience.backend.modules.quickshare.model.SharedItem;
import com.odinlascience.backend.modules.quickshare.repository.SharedItemRepository;
import com.odinlascience.backend.user.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
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
public class QuickShareService extends AbstractOwnedCrudService<SharedItem, SharedItemDTO, CreateTextShareRequest, CreateTextShareRequest> {

    private final SharedItemRepository repository;
    private final SharedItemMapper mapper;
    private final QuickShareFileService fileService;

    private static final String SHARE_CODE_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int SHARE_CODE_LENGTH = 8;
    private static final SecureRandom RANDOM = new SecureRandom();

    public QuickShareService(UserHelper userHelper, ApplicationEventPublisher eventPublisher,
                             SharedItemRepository repository, SharedItemMapper mapper,
                             QuickShareFileService fileService) {
        super(userHelper, eventPublisher);
        this.repository = repository;
        this.mapper = mapper;
        this.fileService = fileService;
    }

    // ─── Méthodes abstraites ───

    @Override
    protected SharedItem toEntity(CreateTextShareRequest request, User owner) {
        return SharedItem.builder()
                .shareCode(generateShareCode())
                .title(request.getTitle())
                .type(ShareType.TEXT)
                .textContent(HtmlSanitizer.sanitize(request.getTextContent()))
                .maxDownloads(request.getMaxDownloads())
                .expiresAt(request.getExpiresAt())
                .createdAt(Instant.now())
                .owner(owner)
                .recipientEmail(request.getRecipientEmail())
                .downloadCount(0)
                .build();
    }

    @Override
    protected void applyUpdate(SharedItem entity, CreateTextShareRequest request) {
        // Pas de mise a jour pour QuickShare
        throw new UnsupportedOperationException("La mise a jour de partages n'est pas supportee");
    }

    @Override
    protected SharedItemDTO toDTO(SharedItem entity) {
        return mapper.toDTO(entity);
    }

    @Override
    public Class<SharedItemDTO> getDtoClass() {
        return SharedItemDTO.class;
    }

    @Override
    protected String getEntityName() {
        return "Partage";
    }

    @Override
    protected String getModuleSlug() {
        return "quickshare";
    }

    @Override
    protected JpaRepository<SharedItem, Long> getRepository() {
        return repository;
    }

    @Override
    protected List<SharedItem> findAllByOwner(User owner) {
        return repository.findByOwnerIdAndDeletedAtIsNullOrderByCreatedAtDesc(owner.getId());
    }

    @Override
    protected List<SharedItem> searchByOwner(String query, Long ownerId) {
        return repository.searchByOwner(ownerId, query);
    }

    @Override
    protected Page<SharedItem> findAllByOwnerPaged(User owner, Pageable pageable) {
        return Page.empty(pageable);
    }

    @Override
    protected Page<SharedItem> searchByOwnerPaged(String query, Long ownerId, Pageable pageable) {
        return Page.empty(pageable);
    }

    // ─── Créer un partage de texte ───

    @Transactional
    public SharedItemDTO createTextShare(CreateTextShareRequest request, String userEmail) {
        User owner = userHelper.findByEmail(userEmail);

        SharedItem item = toEntity(request, owner);
        SharedItem saved = repository.save(item);
        log.info("Text share created: code={}, owner={}", saved.getShareCode(), userEmail);

        SharedItemDTO dto = mapper.toDTO(saved);
        publishAction("CREATE", saved.getId(), null, toJson(dto), "create", "plus", userEmail);
        publishShareEvent(request.getRecipientEmail(), owner, saved);
        return dto;
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

        SharedItemDTO dto = mapper.toDTO(saved);
        publishAction("CREATE", saved.getId(), null, toJson(dto), "create", "plus", userEmail);
        publishShareEvent(recipientEmail, owner, saved);
        return dto;
    }

    // ─── Consultation publique par code ───

    @Transactional(readOnly = true)
    public SharedItemDTO getByShareCode(String shareCode) {
        SharedItem item = repository.findByShareCodeAndDeletedAtIsNull(shareCode)
                .orElseThrow(() -> new ResourceNotFoundException("Aucun partage trouvé avec le code : " + shareCode));
        return mapper.toDTO(item);
    }

    // ─── Enregistrer une consultation ───

    @Transactional
    public void recordView(String shareCode) {
        SharedItem item = repository.findByShareCodeAndDeletedAtIsNull(shareCode)
                .orElseThrow(() -> new ResourceNotFoundException("Aucun partage trouvé avec le code : " + shareCode));

        if (item.isExpired() || item.isDownloadLimitReached()) return;

        item.incrementDownloadCount();
        repository.save(item);
    }

    // ─── Helpers ───

    private String generateShareCode() {
        StringBuilder sb = new StringBuilder(SHARE_CODE_LENGTH);
        for (int i = 0; i < SHARE_CODE_LENGTH; i++) {
            sb.append(SHARE_CODE_CHARS.charAt(RANDOM.nextInt(SHARE_CODE_CHARS.length())));
        }
        String code = sb.toString();
        if (repository.findByShareCodeAndDeletedAtIsNull(code).isPresent()) {
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
