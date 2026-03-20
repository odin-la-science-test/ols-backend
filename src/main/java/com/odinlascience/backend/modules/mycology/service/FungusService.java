package com.odinlascience.backend.modules.mycology.service;

import com.odinlascience.backend.modules.mycology.dto.FungusDTO;
import com.odinlascience.backend.modules.mycology.mapper.FungusMapper;
import com.odinlascience.backend.modules.mycology.model.Fungus;
import com.odinlascience.backend.modules.mycology.repository.FungusRepository;
import com.odinlascience.backend.modules.common.model.IdentifiableMatch;
import com.odinlascience.backend.modules.common.service.AbstractIdentificationService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FungusService extends AbstractIdentificationService<Fungus, FungusDTO, FungusRepository> {
    
    private final FungusMapper mapper;

    public FungusService(FungusRepository repository, FungusMapper mapper) {
        super(repository);
        this.mapper = mapper;
    }

    @Override
    protected FungusDTO toDTO(Fungus fungus) {
        return mapper.toDTO(fungus);
    }

    @Override
    protected FungusDTO toDTO(Fungus fungus, Integer score) {
        return mapper.toDTO(fungus, score);
    }

    @Override
    protected String getEntityName() {
        return "Champignon";
    }

    @Override
    protected List<IdentifiableMatch<Fungus>> findBestMatches(FungusDTO criteria, int limit) {
        return repository.findBestMatches(criteria, limit);
    }

    @Override
    protected Optional<Fungus> findByApiCode(String apiCode) {
        return repository.findByApiCodes_Code(apiCode);
    }

    @Override
    protected List<Fungus> findBySpeciesContaining(String query) {
        return repository.findBySpeciesContainingIgnoreCase(query);
    }

    // Méthodes legacy pour la compatibilité avec les tests existants
    public FungusDTO getFungusById(Long id) {
        return getById(id);
    }

    public FungusDTO getFungusByApiCode(String apiCode) {
        return getByApiCode(apiCode);
    }

    public List<FungusDTO> getAllFungi() {
        return getAll();
    }

    public List<FungusDTO> identifyByProfile(FungusDTO criteria) {
        return identifyByCriteria(criteria);
    }
}