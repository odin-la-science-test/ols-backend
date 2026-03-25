package com.odinlascience.backend.modules.mycology.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.odinlascience.backend.modules.mycology.model.Fungus;

import java.util.List;
import java.util.Optional;

@Repository
public interface FungusRepository extends JpaRepository<Fungus, Long>, FungusRepositoryCustom {
    List<Fungus> findBySpeciesContainingIgnoreCase(String species);

    Optional<Fungus> findByApiCodes_Code(String code);
}
