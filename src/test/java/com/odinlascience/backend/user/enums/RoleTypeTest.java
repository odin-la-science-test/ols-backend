package com.odinlascience.backend.user.enums;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RoleTypeTest {

    @Test
    void valuesContainExpected() {
        RoleType[] values = RoleType.values();
        assertTrue(values.length >= 4);
        assertEquals("GUEST", RoleType.GUEST.name());
        assertEquals("ADMIN", RoleType.ADMIN.name());
    }

}
