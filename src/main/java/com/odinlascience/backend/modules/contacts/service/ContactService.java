package com.odinlascience.backend.modules.contacts.service;

import com.odinlascience.backend.exception.ResourceNotFoundException;
import com.odinlascience.backend.modules.common.service.UserHelper;
import com.odinlascience.backend.modules.contacts.dto.ContactDTO;
import com.odinlascience.backend.modules.contacts.dto.CreateContactRequest;
import com.odinlascience.backend.modules.contacts.dto.UpdateContactRequest;
import com.odinlascience.backend.modules.contacts.mapper.ContactMapper;
import com.odinlascience.backend.modules.contacts.model.Contact;
import com.odinlascience.backend.modules.contacts.repository.ContactRepository;
import com.odinlascience.backend.modules.common.spi.UserQuerySPI;
import com.odinlascience.backend.user.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

/**
 * Service principal du module Contacts.
 * Gère le CRUD complet du carnet de contacts.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ContactService {

    private final ContactRepository repository;
    private final ContactMapper mapper;
    private final UserQuerySPI userQuerySPI;
    private final UserHelper userHelper;

    // ─── Créer un contact ───

    @Transactional
    public ContactDTO create(CreateContactRequest request, String userEmail) {
        User owner = userHelper.findByEmail(userEmail);

        Contact contact = Contact.builder()
                .firstName(request.getFirstName() != null ? request.getFirstName() : "")
                .lastName(request.getLastName() != null ? request.getLastName() : "")
                .email(request.getEmail())
                .phone(request.getPhone())
                .organization(request.getOrganization())
                .jobTitle(request.getJobTitle())
                .notes(request.getNotes())
                .favorite(request.getFavorite() != null ? request.getFavorite() : false)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .owner(owner)
                .build();

        Contact saved = repository.save(contact);
        log.info("Contact created: id={}, name='{} {}', owner={}", saved.getId(), saved.getFirstName(), saved.getLastName(), userEmail);
        return mapper.toDTO(saved, isAppUser(saved.getEmail()));
    }

    // ─── Lister mes contacts ───

    @Transactional(readOnly = true)
    public List<ContactDTO> getMyContacts(String userEmail) {
        User owner = userHelper.findByEmail(userEmail);
        List<Contact> contacts = repository.findByOwnerIdOrderByFavoriteDescLastNameAscFirstNameAsc(owner.getId());
        return contacts.stream()
                .map(c -> mapper.toDTO(c, isAppUser(c.getEmail())))
                .toList();
    }

    // ─── Détail d'un contact par ID ───

    @Transactional(readOnly = true)
    public ContactDTO getById(Long id, String userEmail) {
        Contact contact = findContactOwnedBy(id, userEmail);
        return mapper.toDTO(contact, isAppUser(contact.getEmail()));
    }

    // ─── Mettre à jour un contact ───

    @Transactional
    public ContactDTO update(Long id, UpdateContactRequest request, String userEmail) {
        Contact contact = findContactOwnedBy(id, userEmail);

        if (request.getFirstName() != null) {
            contact.setFirstName(request.getFirstName());
        }
        if (request.getLastName() != null) {
            contact.setLastName(request.getLastName());
        }
        if (request.getEmail() != null) {
            contact.setEmail(request.getEmail());
        }
        if (request.getPhone() != null) {
            contact.setPhone(request.getPhone());
        }
        if (request.getOrganization() != null) {
            contact.setOrganization(request.getOrganization());
        }
        if (request.getJobTitle() != null) {
            contact.setJobTitle(request.getJobTitle());
        }
        if (request.getNotes() != null) {
            contact.setNotes(request.getNotes());
        }
        if (request.getFavorite() != null) {
            contact.setFavorite(request.getFavorite());
        }

        contact.setUpdatedAt(Instant.now());
        Contact saved = repository.save(contact);
        log.info("Contact updated: id={}, owner={}", saved.getId(), userEmail);
        return mapper.toDTO(saved, isAppUser(saved.getEmail()));
    }

    // ─── Supprimer un contact ───

    @Transactional
    public void delete(Long id, String userEmail) {
        Contact contact = findContactOwnedBy(id, userEmail);
        repository.delete(contact);
        log.info("Contact deleted: id={}, owner={}", id, userEmail);
    }

    // ─── Toggle favori ───

    @Transactional
    public ContactDTO toggleFavorite(Long id, String userEmail) {
        Contact contact = findContactOwnedBy(id, userEmail);
        contact.setFavorite(!contact.getFavorite());
        contact.setUpdatedAt(Instant.now());
        Contact saved = repository.save(contact);
        log.info("Contact favorite toggled: id={}, favorite={}, owner={}", saved.getId(), saved.getFavorite(), userEmail);
        return mapper.toDTO(saved, isAppUser(saved.getEmail()));
    }

    // ─── Recherche ───

    @Transactional(readOnly = true)
    public List<ContactDTO> search(String query, String userEmail) {
        User owner = userHelper.findByEmail(userEmail);
        List<Contact> contacts = repository.searchByOwner(owner.getId(), query);
        return contacts.stream()
                .map(c -> mapper.toDTO(c, isAppUser(c.getEmail())))
                .toList();
    }

    // ─── Auto-ajout lors d'une interaction inter-modules ───

    /**
     * Cree automatiquement le contact a partir d'un email cible.
     * Utilisable par n'importe quel event listener inter-modules.
     *
     * @param ownerEmail  email du proprietaire du carnet
     * @param targetEmail email de la personne a ajouter
     */
    @Transactional
    public void ensureContactExistsByEmail(String ownerEmail, String targetEmail) {
        if (targetEmail == null || targetEmail.isBlank()) return;
        if (ownerEmail.equalsIgnoreCase(targetEmail)) return;

        User owner = userHelper.findByEmail(ownerEmail);

        if (repository.findByOwnerIdAndEmail(owner.getId(), targetEmail).isPresent()) return;

        User targetUser = userQuerySPI.findByEmail(targetEmail).orElse(null);

        Contact contact = Contact.builder()
                .firstName(targetUser != null && targetUser.getFirstName() != null ? targetUser.getFirstName() : "")
                .lastName(targetUser != null && targetUser.getLastName() != null ? targetUser.getLastName() : "")
                .email(targetEmail)
                .favorite(false)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .owner(owner)
                .build();

        repository.save(contact);
        log.info("Contact auto-created: email={}, owner={}", targetEmail, ownerEmail);
    }

    // ─── Helpers ───

    private boolean isAppUser(String email) {
        return email != null && !email.isBlank() && userQuerySPI.existsByEmail(email);
    }

    private Contact findContactOwnedBy(Long contactId, String userEmail) {
        Contact contact = repository.findById(contactId)
                .orElseThrow(() -> new ResourceNotFoundException("Contact introuvable avec l'ID : " + contactId));
        return userHelper.verifyOwnership(contact, userEmail, "Contact", contactId);
    }
}
