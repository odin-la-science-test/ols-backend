package com.odinlascience.backend.modules.quickshare.repository;

import com.odinlascience.backend.modules.quickshare.model.SharedItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SharedItemRepository extends JpaRepository<SharedItem, Long> {

    /** Trouver un élément par son code de partage */
    Optional<SharedItem> findByShareCode(String shareCode);

    /** Tous les partages d'un utilisateur, triés par date de création décroissante */
    List<SharedItem> findByOwnerIdOrderByCreatedAtDesc(Long ownerId);

    /** Supprimer les éléments expirés */
    List<SharedItem> findByExpiresAtBefore(LocalDateTime dateTime);
}
