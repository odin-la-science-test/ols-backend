package com.odinlascience.backend.modules.contacts.dto;

import lombok.*;

/**
 * DTO de requête pour mettre à jour un contact.
 * Tous les champs sont optionnels (mise à jour partielle).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateContactRequest {

    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String organization;
    private String jobTitle;
    private String notes;
    private Boolean favorite;
}
