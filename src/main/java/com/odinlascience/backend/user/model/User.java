package com.odinlascience.backend.user.model;

import com.odinlascience.backend.user.enums.AuthProvider;
import com.odinlascience.backend.user.enums.RoleType;

import jakarta.persistence.*;
import lombok.*;

@Data
@Entity
@Table(name = "app_users")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;
    private String password; 

    private String firstName;
    private String lastName;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private RoleType role = RoleType.GUEST;

    private String avatarId;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private AuthProvider authProvider = AuthProvider.LOCAL;

    private String externalId;

    /** Retourne le nom complet (prénom + nom), trimé. */
    public String getFullName() {
        String first = firstName != null ? firstName : "";
        String last = lastName != null ? lastName : "";
        return (first + " " + last).trim();
    }
}