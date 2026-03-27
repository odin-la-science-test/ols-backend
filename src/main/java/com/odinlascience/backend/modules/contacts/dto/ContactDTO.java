package com.odinlascience.backend.modules.contacts.dto;

import lombok.*;

import java.time.Instant;

/**
 * DTO de réponse pour un contact.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContactDTO {

    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String organization;
    private String jobTitle;
    private String notes;
    private Boolean favorite;

    private Instant createdAt;
    private Instant updatedAt;

    /** Nom du propriétaire du carnet */
    private String ownerName;

    /** Indique si l'email correspond à un utilisateur inscrit sur OLS */
    private Boolean isAppUser;

    /** ID de l'utilisateur OLS correspondant (null si pas un app user) */
    private Long appUserId;

    /** Avatar de l'utilisateur OLS correspondant (null si pas un app user) */
    private String appUserAvatarId;
}
