package com.odinlascience.backend.modules.organization.model;

import com.odinlascience.backend.user.model.User;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "supervision_relationships", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"organization_id", "supervisor_id", "supervisee_id"})
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SupervisionRelationship {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id", nullable = false)
    private Organization organization;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supervisor_id", nullable = false)
    private User supervisor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supervisee_id", nullable = false)
    private User supervisee;

    @Builder.Default
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();
}
