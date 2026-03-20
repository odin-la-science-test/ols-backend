package com.odinlascience.backend.user.model;

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
}