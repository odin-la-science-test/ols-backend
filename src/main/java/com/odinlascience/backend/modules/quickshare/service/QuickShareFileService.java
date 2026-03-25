package com.odinlascience.backend.modules.quickshare.service;

import com.odinlascience.backend.exception.ResourceNotFoundException;
import com.odinlascience.backend.modules.quickshare.enums.ShareType;
import com.odinlascience.backend.modules.quickshare.model.SharedFile;
import com.odinlascience.backend.modules.quickshare.model.SharedItem;
import com.odinlascience.backend.modules.quickshare.repository.SharedFileRepository;
import com.odinlascience.backend.modules.quickshare.repository.SharedItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Gere les operations fichier de QuickShare : stockage, telechargement, ZIP, suppression physique.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class QuickShareFileService {

    private final SharedItemRepository itemRepository;
    private final SharedFileRepository fileRepository;

    @Value("${quickshare.upload-dir:./uploads/quickshare}")
    private String uploadDir;

    @Value("${quickshare.max-file-size:52428800}")
    private long maxFileSize;

    /**
     * Stocke les fichiers sur disque et les associe au SharedItem.
     */
    public void storeFiles(MultipartFile[] files, SharedItem item) throws IOException {
        if (files == null || files.length == 0) {
            throw new IllegalArgumentException("Aucun fichier fourni");
        }

        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        for (MultipartFile file : files) {
            if (file.isEmpty()) continue;
            if (file.getSize() > maxFileSize) {
                throw new IllegalArgumentException(
                        "Le fichier '" + file.getOriginalFilename() + "' dépasse la taille maximale autorisée");
            }

            String storedFilename = buildStoredFilename(file.getOriginalFilename());
            Path targetPath = uploadPath.resolve(storedFilename);
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

            SharedFile sharedFile = SharedFile.builder()
                    .originalFilename(file.getOriginalFilename())
                    .storedFilename(storedFilename)
                    .contentType(file.getContentType())
                    .fileSize(file.getSize())
                    .sharedItem(item)
                    .build();

            item.getFiles().add(sharedFile);
        }
    }

    /**
     * Telecharge un fichier specifique par shareCode et fileId.
     */
    @Transactional(readOnly = true)
    public Resource downloadFile(String shareCode, Long fileId) {
        SharedItem item = findFileShare(shareCode);

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

    /**
     * Recupere un SharedFile par son ID (pour headers de telechargement).
     */
    public SharedFile getSharedFile(Long fileId) {
        return fileRepository.findById(fileId)
                .orElseThrow(() -> new ResourceNotFoundException("Fichier introuvable"));
    }

    /**
     * Ecrit un ZIP contenant tous les fichiers d'un partage.
     */
    @Transactional(readOnly = true)
    public void writeZip(String shareCode, OutputStream outputStream) throws IOException {
        SharedItem item = findFileShare(shareCode);
        Path uploadPath = Paths.get(uploadDir);

        try (ZipOutputStream zos = new ZipOutputStream(outputStream)) {
            for (SharedFile file : item.getFiles()) {
                Path filePath = uploadPath.resolve(file.getStoredFilename()).normalize();
                if (!Files.exists(filePath)) {
                    log.warn("Fichier physique introuvable pour le ZIP: {}", file.getStoredFilename());
                    continue;
                }
                zos.putNextEntry(new ZipEntry(file.getOriginalFilename()));
                Files.copy(filePath, zos);
                zos.closeEntry();
            }
        }
    }

    /**
     * Nom du ZIP base sur le titre ou le code.
     */
    @Transactional(readOnly = true)
    public String getZipFilename(String shareCode) {
        SharedItem item = itemRepository.findByShareCode(shareCode)
                .orElseThrow(() -> new ResourceNotFoundException("Aucun partage trouvé"));
        String base = item.getTitle() != null && !item.getTitle().isBlank()
                ? item.getTitle().replaceAll("[^a-zA-Z0-9À-ÿ _-]", "")
                : "quickshare-" + shareCode;
        return base + ".zip";
    }

    /**
     * Supprime les fichiers physiques d'un partage.
     */
    public void deleteFiles(SharedItem item) {
        if (item.getType() != ShareType.FILE || item.getFiles() == null) return;

        for (SharedFile file : item.getFiles()) {
            try {
                Path filePath = Paths.get(uploadDir).resolve(file.getStoredFilename());
                Files.deleteIfExists(filePath);
            } catch (IOException e) {
                log.warn("Impossible de supprimer le fichier physique: {}", file.getStoredFilename(), e);
            }
        }
    }

    // ─── Helpers prives ───

    private SharedItem findFileShare(String shareCode) {
        SharedItem item = itemRepository.findByShareCode(shareCode)
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
        return item;
    }

    private String buildStoredFilename(String originalFilename) {
        String storedFilename = UUID.randomUUID().toString();
        if (originalFilename != null && originalFilename.contains(".")) {
            String ext = originalFilename.substring(originalFilename.lastIndexOf("."));
            storedFilename += ext;
        }
        return storedFilename;
    }
}
