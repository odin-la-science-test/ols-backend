package com.odinlascience.backend.modules.common.model;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Représente un code d'identification API (bioMérieux) avec sa galerie associée.
 * 
 * Les galeries API courantes :
 * - Bactériologie : API 20 E, API 20 NE, API Staph, API Strep, API Coryne, API 50 CH
 * - Mycologie : API 20 C AUX, API ID 32 C
 */
@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiCode {
    
    /** Type de galerie utilisée (ex: "API 20 E", "API Staph", "API 20 C AUX") */
    private String gallery;
    
    /** Code numérique à 7 chiffres (ex: "5144572") */
    private String code;
}
