package com.odinlascience.backend.exception;

import com.odinlascience.backend.user.enums.AuthProvider;
import lombok.Getter;

@Getter
public class AuthProviderConflictException extends RuntimeException {

    private final AuthProvider existingProvider;

    public AuthProviderConflictException(AuthProvider existingProvider) {
        super("Ce compte utilise une autre methode de connexion : " + existingProvider);
        this.existingProvider = existingProvider;
    }
}
