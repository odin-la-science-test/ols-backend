package com.odinlascience.backend.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO pour la confirmation de suppression de compte.
 * L'email doit correspondre a celui du compte authentifie.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeleteAccountRequest {

    @NotBlank(message = "L'email de confirmation est requis")
    @Email(message = "L'email de confirmation doit etre valide")
    private String confirmEmail;
}
