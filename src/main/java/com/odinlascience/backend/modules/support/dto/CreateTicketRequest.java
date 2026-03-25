package com.odinlascience.backend.modules.support.dto;

import com.odinlascience.backend.modules.support.enums.TicketCategory;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * DTO de requête pour créer un ticket de support.
 * La priorité est fixée automatiquement à MEDIUM.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateTicketRequest {

    /** Sujet du ticket (requis) */
    @NotBlank(message = "Le sujet est requis")
    private String subject;

    /** Description détaillée */
    private String description;

    /** Catégorie du ticket (requis) */
    @NotNull(message = "La catégorie est requise")
    private TicketCategory category;
}
