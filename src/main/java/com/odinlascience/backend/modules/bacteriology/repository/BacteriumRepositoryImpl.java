package com.odinlascience.backend.modules.bacteriology.repository;

import com.odinlascience.backend.modules.bacteriology.dto.BacteriumDTO;
import com.odinlascience.backend.modules.bacteriology.model.Bacterium;
import com.odinlascience.backend.modules.common.repository.AbstractIdentificationRepository;
import org.springframework.stereotype.Repository;

@Repository
public class BacteriumRepositoryImpl extends AbstractIdentificationRepository<Bacterium, BacteriumDTO> {

    @Override
    protected Class<Bacterium> getEntityClass() {
        return Bacterium.class;
    }

    @Override
    protected Class<BacteriumDTO> getDtoClass() {
        return BacteriumDTO.class;
    }
}