package com.odinlascience.backend.modules.bacteriology.service;

import com.odinlascience.backend.modules.bacteriology.dto.BacteriumDTO;
import com.odinlascience.backend.modules.bacteriology.mapper.BacteriumMapper;
import com.odinlascience.backend.modules.bacteriology.model.Bacterium;
import com.odinlascience.backend.modules.bacteriology.repository.BacteriumRepository;
import com.odinlascience.backend.modules.common.model.IdentifiableMatch;
import com.odinlascience.backend.modules.common.service.AbstractIdentificationService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BacteriumService extends AbstractIdentificationService<Bacterium, BacteriumDTO, BacteriumRepository> {
    
    private final BacteriumMapper mapper;

    public BacteriumService(BacteriumRepository repository, BacteriumMapper mapper) {
        super(repository);
        this.mapper = mapper;
    }

    @Override
    protected BacteriumDTO toDTO(Bacterium bacterium) {
        return mapper.toDTO(bacterium);
    }

    @Override
    protected BacteriumDTO toDTO(Bacterium bacterium, Integer score) {
        return mapper.toDTO(bacterium, score);
    }

    @Override
    protected String getEntityName() {
        return "Bactérie";
    }

    @Override
    protected List<IdentifiableMatch<Bacterium>> findBestMatches(BacteriumDTO criteria, int limit) {
        return repository.findBestMatches(criteria, limit);
    }

    @Override
    protected Optional<Bacterium> findByApiCode(String apiCode) {
        return repository.findByApiCodes_Code(apiCode);
    }

    @Override
    protected List<Bacterium> findBySpeciesContaining(String query) {
        return repository.findBySpeciesContainingIgnoreCase(query);
    }

    // Méthode legacy pour la compatibilité avec les tests existants
    public BacteriumDTO getBacteriumById(Long id) {
        return getById(id);
    }

    public BacteriumDTO getBacteriumByApiCode(String apiCode) {
        return getByApiCode(apiCode);
    }

    public List<BacteriumDTO> getAllBacteria() {
        return getAll();
    }

    public List<BacteriumDTO> identifyByBiochemicalProfile(BacteriumDTO criteria) {
        return identifyByCriteria(criteria);
    }
}