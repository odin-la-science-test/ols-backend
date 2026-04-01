package com.odinlascience.backend.modules.mycology.repository;

import com.odinlascience.backend.modules.mycology.dto.FungusDTO;
import com.odinlascience.backend.modules.mycology.model.Fungus;
import com.odinlascience.backend.modules.common.repository.IdentificationRepositoryCustom;

public interface FungusRepositoryCustom extends IdentificationRepositoryCustom<Fungus, FungusDTO> {
}
