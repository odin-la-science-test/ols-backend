package com.odinlascience.backend.modules.mycology.repository;

import com.odinlascience.backend.modules.mycology.dto.FungusDTO;
import com.odinlascience.backend.modules.mycology.model.Fungus;
import com.odinlascience.backend.modules.common.repository.AbstractIdentificationRepository;
import org.springframework.stereotype.Repository;

@Repository
public class FungusRepositoryImpl extends AbstractIdentificationRepository<Fungus, FungusDTO> {

    @Override
    protected Class<Fungus> getEntityClass() {
        return Fungus.class;
    }

    @Override
    protected Class<FungusDTO> getDtoClass() {
        return FungusDTO.class;
    }
}
