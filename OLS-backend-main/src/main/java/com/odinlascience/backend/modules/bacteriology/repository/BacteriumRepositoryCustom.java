package com.odinlascience.backend.modules.bacteriology.repository;

import com.odinlascience.backend.modules.bacteriology.dto.BacteriumDTO;
import com.odinlascience.backend.modules.bacteriology.model.Bacterium;
import com.odinlascience.backend.modules.common.repository.IdentificationRepositoryCustom;

public interface BacteriumRepositoryCustom extends IdentificationRepositoryCustom<Bacterium, BacteriumDTO> {
}