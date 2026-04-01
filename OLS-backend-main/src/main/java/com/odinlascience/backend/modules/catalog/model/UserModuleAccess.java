package com.odinlascience.backend.modules.catalog.model;

import com.odinlascience.backend.user.model.User;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "user_module_access", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"user_id", "module_id"})
})
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserModuleAccess {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "module_id", nullable = false)
    private AppModule module;

    @Builder.Default
    private Boolean hasAccess = true;

    @Column(name = "purchased_at")
    private LocalDateTime purchasedAt;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    @PrePersist
    protected void onCreate() {
        if (purchasedAt == null) {
            purchasedAt = LocalDateTime.now();
        }
    }
}
