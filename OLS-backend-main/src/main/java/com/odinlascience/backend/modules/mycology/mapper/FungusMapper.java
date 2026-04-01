package com.odinlascience.backend.modules.mycology.mapper;

import com.odinlascience.backend.modules.mycology.dto.FungusDTO;
import com.odinlascience.backend.modules.mycology.model.Fungus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface FungusMapper {

    @Mapping(target = "confidenceScore", ignore = true)
    FungusDTO toDTO(Fungus fungus);

    @Mapping(source = "score", target = "confidenceScore")
    FungusDTO toDTO(Fungus fungus, Integer score);
}
