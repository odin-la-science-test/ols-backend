package com.odinlascience.backend.modules.catalog.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class UserModuleAccessTest {

    @Test
    void builder_CreatesInstanceWithDefaults() {
        UserModuleAccess access = UserModuleAccess.builder().build();
        assertThat(access.getHasAccess()).isTrue();
    }

    @Test
    void onCreate_SetsPurchasedAtIfNull() {
        UserModuleAccess access = new UserModuleAccess();
        access.onCreate();
        assertThat(access.getPurchasedAt()).isNotNull();
    }

    @Test
    void onCreate_DoesNotOverridePurchasedAt() {
        LocalDateTime customDate = LocalDateTime.of(2024, 1, 1, 0, 0);
        UserModuleAccess access = new UserModuleAccess();
        access.setPurchasedAt(customDate);
        access.onCreate();
        assertThat(access.getPurchasedAt()).isEqualTo(customDate);
    }
}
