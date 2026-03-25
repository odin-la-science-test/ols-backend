package com.odinlascience.backend.modules.contacts.repository;

import com.odinlascience.backend.modules.contacts.model.Contact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ContactRepository extends JpaRepository<Contact, Long> {

    /** Tous les contacts d'un utilisateur, favoris en premier, puis par nom alphabétique */
    List<Contact> findByOwnerIdOrderByFavoriteDescLastNameAscFirstNameAsc(Long ownerId);

    /** Vérifie si un contact existe déjà pour cet owner avec cet email */
    Optional<Contact> findByOwnerIdAndEmail(Long ownerId, String email);

    /** Recherche par nom, prénom, email ou organisation (insensible à la casse) */
    @Query("SELECT c FROM Contact c WHERE c.owner.id = :ownerId " +
           "AND (LOWER(c.firstName) LIKE LOWER(CONCAT('%', :query, '%')) " +
           "OR LOWER(c.lastName) LIKE LOWER(CONCAT('%', :query, '%')) " +
           "OR LOWER(c.email) LIKE LOWER(CONCAT('%', :query, '%')) " +
           "OR LOWER(c.organization) LIKE LOWER(CONCAT('%', :query, '%')) " +
           "OR LOWER(c.jobTitle) LIKE LOWER(CONCAT('%', :query, '%'))) " +
           "ORDER BY c.favorite DESC, c.lastName ASC, c.firstName ASC")
    List<Contact> searchByOwner(@Param("ownerId") Long ownerId, @Param("query") String query);
}
