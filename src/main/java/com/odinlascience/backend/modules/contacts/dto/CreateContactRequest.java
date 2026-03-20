package com.odinlascience.backend.modules.contacts.dto;

import lombok.*;

/**
 * DTO de requête pour créer un contact.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateContactRequest {

    /** Prénom */
    private String firstName;

    /** Nom */
    private String lastName;

    /** Email du contact */
    private String email;

    /** Téléphone */
    private String phone;

    /** Organisation / Labo */
    private String organization;

    /** Titre / Fonction */
    private String jobTitle;

    /** Notes libres */
    private String notes;

    /** Favori */
    private Boolean favorite;
}
