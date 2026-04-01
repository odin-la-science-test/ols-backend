package com.odinlascience.backend.modules.quickshare.repository;

import com.odinlascience.backend.modules.quickshare.model.SharedFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SharedFileRepository extends JpaRepository<SharedFile, Long> {
}
