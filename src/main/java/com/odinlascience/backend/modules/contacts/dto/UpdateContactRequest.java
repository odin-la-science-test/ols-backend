package com.odinlascience.backend.modules.contacts.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
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

    @Size(max = 255)
    private String firstName;

    @Size(max = 255)
    private String lastName;

    @Email
    @Size(max = 255)
    private String email;

    @Size(max = 30)
    private String phone;

    @Size(max = 255)
    private String organization;

    @Size(max = 255)
    private String jobTitle;

    private String notes;
    private Boolean favorite;
}
