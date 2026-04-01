package com.odinlascience.backend.modules.organization.repository;

import com.odinlascience.backend.modules.organization.model.Organization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrganizationRepository extends JpaRepository<Organization, Long> {

    @Query("SELECT o FROM Organization o " +
           "JOIN OrganizationMembership m ON m.organization = o " +
           "WHERE m.user.id = :userId AND m.status = 'ACTIVE' " +
           "ORDER BY o.name ASC")
    List<Organization> findByActiveMembership(@Param("userId") Long userId);

    @Query("SELECT o FROM Organization o " +
           "JOIN OrganizationMembership m ON m.organization = o " +
           "WHERE m.user.id = :userId AND m.status = 'ACTIVE' " +
           "AND LOWER(o.name) LIKE LOWER(CONCAT('%', :query, '%')) " +
           "ORDER BY o.name ASC")
    List<Organization> searchByActiveMembership(
            @Param("userId") Long userId,
            @Param("query") String query);
}
