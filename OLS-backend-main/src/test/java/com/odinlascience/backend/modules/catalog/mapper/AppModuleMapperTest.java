package com.odinlascience.backend.modules.catalog.mapper;

import com.odinlascience.backend.modules.catalog.dto.AppModuleDTO;
import com.odinlascience.backend.modules.catalog.enums.ModuleType;
import com.odinlascience.backend.modules.catalog.model.AppModule;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AppModuleMapperTest {

    private AppModuleMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = org.mapstruct.factory.Mappers.getMapper(AppModuleMapper.class);
    }

    @Test
    void toDTO_NullInput_ReturnsNull() {
        assertNull(mapper.toDTO((AppModule) null));
    }

    @Test
    void toDTO_FullMapping_MapsFieldsAndDefaults() {
        AppModule m = AppModule.builder()
                .id(10L)
                .moduleKey("key-1")
                .price(5.5)
                .type(ModuleType.HUGIN_LAB)
                .build();

        AppModuleDTO dto = mapper.toDTO(m);

        assertNotNull(dto);
        assertEquals(m.getModuleKey(), dto.getModuleKey());
        assertEquals(m.getPrice(), dto.getPrice());
        assertFalse(dto.isLocked());
    }

    @Test
    void toDTO_NullAppModule_ButLockedProvided_ReturnsDtoWithLocked() {
        AppModuleDTO dto = mapper.toDTO(null, Boolean.TRUE);
        assertNotNull(dto);
        assertTrue(dto.isLocked());
        assertNull(dto.getModuleKey());
    }

    @Test
    void toDTO_AppModuleWithNullLocked_MapsFieldsAndLockedDefault() {
        AppModule m = AppModule.builder()
                .moduleKey("k2")
                .price(0.0)
                .type(ModuleType.MUNIN_ATLAS)
                .build();

        AppModuleDTO dto = mapper.toDTO(m, null);
        assertNotNull(dto);
        assertEquals(m.getModuleKey(), dto.getModuleKey());
        assertFalse(dto.isLocked());
    }

    @Test
    void toDTO_BothNonNull_MapsFieldsAndLocked() {
        AppModule m = AppModule.builder()
                .moduleKey("k3")
                .price(1.1)
                .type(ModuleType.MUNIN_ATLAS)
                .build();

        AppModuleDTO dto = mapper.toDTO(m, Boolean.TRUE);
        assertNotNull(dto);
        assertEquals(m.getModuleKey(), dto.getModuleKey());
        assertTrue(dto.isLocked());
    }

    @Test
    void toDTO_BothNull_ReturnsNull() {
        assertNull(mapper.toDTO((AppModule) null, (Boolean) null));
    }
}
