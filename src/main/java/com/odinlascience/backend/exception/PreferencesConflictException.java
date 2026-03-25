package com.odinlascience.backend.exception;

import com.odinlascience.backend.user.dto.UserPreferencesDTO;
import lombok.Getter;

@Getter
public class PreferencesConflictException extends RuntimeException {

    private final UserPreferencesDTO serverPreferences;

    public PreferencesConflictException(UserPreferencesDTO serverPreferences) {
        super("Les preferences du serveur sont plus recentes");
        this.serverPreferences = serverPreferences;
    }
}
