package com.odinlascience.backend.modules.bacteriology.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.odinlascience.backend.modules.bacteriology.model.Bacterium;

import java.util.List;
import java.util.Optional;

@Repository
public interface BacteriumRepository extends JpaRepository<Bacterium, Long>, BacteriumRepositoryCustom {
    List<Bacterium> findBySpeciesContainingIgnoreCase(String species);

    Optional<Bacterium> findByApiCodes_Code(String code);
}