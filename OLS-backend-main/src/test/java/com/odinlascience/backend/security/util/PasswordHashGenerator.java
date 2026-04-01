package com.odinlascience.backend.security.util;

import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Utilitaire pour générer des hash de mots de passe pour data.sql
 * Exécuter comme test JUnit ou via main()
 */
public class PasswordHashGenerator {

    public static void main(String[] args) {
        PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
        
        String password = "1234";
        String encoded = encoder.encode(password);
        
        System.out.println("===========================================");
        System.out.println("Password: " + password);
        System.out.println("Encoded:  " + encoded);
        System.out.println("===========================================");
        System.out.println("SQL INSERT:");
        System.out.println("'" + encoded + "'");
    }
}
