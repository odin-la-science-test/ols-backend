package com.odinlascience.backend.modules.contacts.service;

import com.odinlascience.backend.exception.ResourceNotFoundException;
import com.odinlascience.backend.modules.contacts.dto.ContactDTO;
import com.odinlascience.backend.modules.contacts.dto.CreateContactRequest;
import com.odinlascience.backend.modules.contacts.dto.UpdateContactRequest;
import com.odinlascience.backend.modules.contacts.mapper.ContactMapper;
import com.odinlascience.backend.modules.contacts.model.Contact;
import com.odinlascience.backend.modules.contacts.repository.ContactRepository;
import com.odinlascience.backend.user.model.User;
import com.odinlascience.backend.user.repository.UserRepository;
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
    private final UserRepository userRepository;

    // ─── Créer un contact ───

    @Transactional
    public ContactDTO create(CreateContactRequest request, String userEmail) {
        User owner = findUserByEmail(userEmail);

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
        return mapper.toDTO(saved);
    }

    // ─── Lister mes contacts ───

    @Transactional(readOnly = true)
    public List<ContactDTO> getMyContacts(String userEmail) {
        User owner = findUserByEmail(userEmail);
        return repository.findByOwnerIdOrderByFavoriteDescLastNameAscFirstNameAsc(owner.getId())
                .stream()
                .map(mapper::toDTO)
                .toList();
    }

    // ─── Détail d'un contact par ID ───

    @Transactional(readOnly = true)
    public ContactDTO getById(Long id, String userEmail) {
        Contact contact = findContactOwnedBy(id, userEmail);
        return mapper.toDTO(contact);
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
        return mapper.toDTO(saved);
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
        return mapper.toDTO(saved);
    }

    // ─── Recherche ───

    @Transactional(readOnly = true)
    public List<ContactDTO> search(String query, String userEmail) {
        User owner = findUserByEmail(userEmail);
        return repository.searchByOwner(owner.getId(), query)
                .stream()
                .map(mapper::toDTO)
                .toList();
    }

    // ─── Auto-ajout lors d'un partage direct ───

    /**
     * Crée automatiquement le contact si l'owner n'a pas encore cette adresse email dans ses contacts.
     * Utilisé notamment lors d'un partage QuickShare direct.
     *
     * @param ownerEmail   email du propriétaire du carnet
     * @param targetUser   utilisateur OLS cible (fournit prénom, nom, email)
     */
    @Transactional
    public void ensureContactExists(String ownerEmail, User targetUser) {
        if (targetUser.getEmail() == null) return;

        User owner = findUserByEmail(ownerEmail);

        // Ne pas s'ajouter soi-même
        if (owner.getId().equals(targetUser.getId())) return;

        // Déjà présent ?
        if (repository.findByOwnerIdAndEmail(owner.getId(), targetUser.getEmail()).isPresent()) return;

        Contact contact = Contact.builder()
                .firstName(targetUser.getFirstName() != null ? targetUser.getFirstName() : "")
                .lastName(targetUser.getLastName() != null ? targetUser.getLastName() : "")
                .email(targetUser.getEmail())
                .favorite(false)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .owner(owner)
                .build();

        repository.save(contact);
        log.info("Contact auto-created from QuickShare: email={}, owner={}", targetUser.getEmail(), ownerEmail);
    }

    // ─── Helpers ───

    private User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur introuvable : " + email));
    }

    private Contact findContactOwnedBy(Long contactId, String userEmail) {
        Contact contact = repository.findById(contactId)
                .orElseThrow(() -> new ResourceNotFoundException("Contact introuvable avec l'ID : " + contactId));

        User owner = findUserByEmail(userEmail);
        if (!contact.getOwner().getId().equals(owner.getId())) {
            throw new ResourceNotFoundException("Contact introuvable avec l'ID : " + contactId);
        }

        return contact;
    }
}
