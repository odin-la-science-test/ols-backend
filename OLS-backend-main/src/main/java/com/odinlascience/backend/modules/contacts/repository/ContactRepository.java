package com.odinlascience.backend.modules.contacts.repository;

import com.odinlascience.backend.modules.contacts.model.Contact;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ContactRepository extends JpaRepository<Contact, Long> {

    /** Tous les contacts d'un utilisateur (non supprimés), favoris en premier, puis par nom alphabétique */
    List<Contact> findByOwnerIdAndDeletedAtIsNullOrderByFavoriteDescLastNameAscFirstNameAsc(Long ownerId);

    /** Vérifie si un contact existe déjà pour cet owner avec cet email */
    Optional<Contact> findByOwnerIdAndEmail(Long ownerId, String email);

    /** Recherche par nom, prénom, email ou organisation (insensible à la casse, exclut supprimés) */
    @Query("SELECT c FROM Contact c WHERE c.owner.id = :ownerId AND c.deletedAt IS NULL " +
           "AND (LOWER(c.firstName) LIKE LOWER(CONCAT('%', :query, '%')) " +
           "OR LOWER(c.lastName) LIKE LOWER(CONCAT('%', :query, '%')) " +
           "OR LOWER(c.email) LIKE LOWER(CONCAT('%', :query, '%')) " +
           "OR LOWER(c.organization) LIKE LOWER(CONCAT('%', :query, '%')) " +
           "OR LOWER(c.jobTitle) LIKE LOWER(CONCAT('%', :query, '%'))) " +
           "ORDER BY c.favorite DESC, c.lastName ASC, c.firstName ASC")
    List<Contact> searchByOwner(@Param("ownerId") Long ownerId, @Param("query") String query);

    /** Supprimer tous les contacts d'un utilisateur (RGPD) */
    void deleteByOwnerId(Long ownerId);

    /** Version paginée : contacts d'un utilisateur (non supprimés), favoris en premier, puis par nom alphabétique */
    Page<Contact> findByOwnerIdAndDeletedAtIsNullOrderByFavoriteDescLastNameAscFirstNameAsc(Long ownerId, Pageable pageable);

    /** Version paginée : recherche par nom, prénom, email ou organisation (insensible à la casse, exclut supprimés) */
    @Query("SELECT c FROM Contact c WHERE c.owner.id = :ownerId AND c.deletedAt IS NULL " +
           "AND (LOWER(c.firstName) LIKE LOWER(CONCAT('%', :query, '%')) " +
           "OR LOWER(c.lastName) LIKE LOWER(CONCAT('%', :query, '%')) " +
           "OR LOWER(c.email) LIKE LOWER(CONCAT('%', :query, '%')) " +
           "OR LOWER(c.organization) LIKE LOWER(CONCAT('%', :query, '%')) " +
           "OR LOWER(c.jobTitle) LIKE LOWER(CONCAT('%', :query, '%'))) " +
           "ORDER BY c.favorite DESC, c.lastName ASC, c.firstName ASC")
    Page<Contact> searchByOwnerPaged(@Param("ownerId") Long ownerId, @Param("query") String query, Pageable pageable);
}
