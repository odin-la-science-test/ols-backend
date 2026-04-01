package com.odinlascience.backend.modules.contacts.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
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
    @Size(max = 255)
    private String firstName;

    /** Nom */
    @Size(max = 255)
    private String lastName;

    /** Email du contact */
    @Email
    @Size(max = 255)
    private String email;

    /** Téléphone */
    @Size(max = 30)
    private String phone;

    /** Organisation / Labo */
    @Size(max = 255)
    private String organization;

    /** Titre / Fonction */
    @Size(max = 255)
    private String jobTitle;

    /** Notes libres */
    private String notes;

    /** Favori */
    private Boolean favorite;
}
