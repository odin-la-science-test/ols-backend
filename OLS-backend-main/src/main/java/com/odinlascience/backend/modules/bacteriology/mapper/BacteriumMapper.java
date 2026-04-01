package com.odinlascience.backend.modules.bacteriology.mapper;

import com.odinlascience.backend.modules.bacteriology.dto.BacteriumDTO;
import com.odinlascience.backend.modules.bacteriology.model.Bacterium;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BacteriumMapper {

    @Mapping(target = "confidenceScore", ignore = true)
    BacteriumDTO toDTO(Bacterium bacterium);

    @Mapping(source = "score", target = "confidenceScore")
    BacteriumDTO toDTO(Bacterium bacterium, Integer score);
}