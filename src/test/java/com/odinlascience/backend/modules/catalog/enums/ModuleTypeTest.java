package com.odinlascience.backend.modules.catalog.enums;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ModuleTypeTest {

    @Test
    void valuesContainExpected() {
        ModuleType[] values = ModuleType.values();
        assertTrue(values.length >= 2);
        assertEquals("MUNIN_ATLAS", ModuleType.MUNIN_ATLAS.name());
        assertEquals("HUGIN_LAB", ModuleType.HUGIN_LAB.name());
    }

}
