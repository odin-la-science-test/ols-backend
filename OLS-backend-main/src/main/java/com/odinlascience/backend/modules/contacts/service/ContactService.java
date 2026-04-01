package com.odinlascience.backend.modules.contacts.service;

import com.odinlascience.backend.modules.common.service.AbstractOwnedCrudService;
import com.odinlascience.backend.modules.common.service.HtmlSanitizer;
import com.odinlascience.backend.modules.common.service.UserHelper;
import com.odinlascience.backend.modules.common.spi.UserQuerySPI;
import org.springframework.context.ApplicationEventPublisher;
import com.odinlascience.backend.modules.contacts.dto.ContactDTO;
import com.odinlascience.backend.modules.contacts.dto.CreateContactRequest;
import com.odinlascience.backend.modules.contacts.dto.UpdateContactRequest;
import com.odinlascience.backend.modules.contacts.mapper.ContactMapper;
import com.odinlascience.backend.modules.contacts.model.Contact;
import com.odinlascience.backend.modules.contacts.repository.ContactRepository;
import com.odinlascience.backend.user.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service principal du module Contacts.
 * Gère le CRUD complet du carnet de contacts.
 */
@Slf4j
@Service
public class ContactService
        extends AbstractOwnedCrudService<Contact, ContactDTO, CreateContactRequest, UpdateContactRequest> {

    private final ContactRepository repository;
    private final ContactMapper mapper;
    private final UserQuerySPI userQuerySPI;

    public ContactService(UserHelper userHelper, ApplicationEventPublisher eventPublisher,
                          ContactRepository repository, ContactMapper mapper, UserQuerySPI userQuerySPI) {
        super(userHelper, eventPublisher);
        this.repository = repository;
        this.mapper = mapper;
        this.userQuerySPI = userQuerySPI;
    }

    // ─── Méthodes abstraites implémentées ───

    @Override
    protected Contact toEntity(CreateContactRequest request, User owner) {
        return Contact.builder()
                .firstName(request.getFirstName() != null ? request.getFirstName() : "")
                .lastName(request.getLastName() != null ? request.getLastName() : "")
                .email(request.getEmail())
                .phone(request.getPhone())
                .organization(request.getOrganization())
                .jobTitle(request.getJobTitle())
                .notes(HtmlSanitizer.sanitize(request.getNotes()))
                .favorite(request.getFavorite() != null ? request.getFavorite() : false)
                .owner(owner)
                .build();
    }

    @Override
    protected void applyUpdate(Contact entity, UpdateContactRequest request) {
        if (request.getFirstName() != null) entity.setFirstName(request.getFirstName());
        if (request.getLastName() != null) entity.setLastName(request.getLastName());
        if (request.getEmail() != null) entity.setEmail(request.getEmail());
        if (request.getPhone() != null) entity.setPhone(request.getPhone());
        if (request.getOrganization() != null) entity.setOrganization(request.getOrganization());
        if (request.getJobTitle() != null) entity.setJobTitle(request.getJobTitle());
        if (request.getNotes() != null) entity.setNotes(HtmlSanitizer.sanitize(request.getNotes()));
        if (request.getFavorite() != null) entity.setFavorite(request.getFavorite());
    }

    @Override
    protected ContactDTO toDTO(Contact entity) {
        return mapper.toDTO(entity, findAppUser(entity.getEmail()));
    }

    @Override
    public Class<ContactDTO> getDtoClass() {
        return ContactDTO.class;
    }

    @Override
    protected String getEntityName() {
        return "Contact";
    }

    @Override
    protected String getModuleSlug() {
        return "contacts";
    }

    @Override
    protected JpaRepository<Contact, Long> getRepository() {
        return repository;
    }

    @Override
    protected List<Contact> findAllByOwner(User owner) {
        return repository.findByOwnerIdAndDeletedAtIsNullOrderByFavoriteDescLastNameAscFirstNameAsc(owner.getId());
    }

    @Override
    protected List<Contact> searchByOwner(String query, Long ownerId) {
        return repository.searchByOwner(ownerId, query);
    }

    @Override
    protected Page<Contact> findAllByOwnerPaged(User owner, Pageable pageable) {
        return repository.findByOwnerIdAndDeletedAtIsNullOrderByFavoriteDescLastNameAscFirstNameAsc(owner.getId(), pageable);
    }

    @Override
    protected Page<Contact> searchByOwnerPaged(String query, Long ownerId, Pageable pageable) {
        return repository.searchByOwnerPaged(ownerId, query, pageable);
    }

    // ─── Toggle favori ───

    @Transactional
    public ContactDTO toggleFavorite(Long id, String userEmail) {
        Contact contact = findEntityOwnedBy(id, userEmail);
        String previousJson = toJson(mapper.toDTO(contact, findAppUser(contact.getEmail())));
        contact.setFavorite(!contact.getFavorite());
        Contact saved = repository.save(contact);
        log.info("Contact favorite toggled: id={}, favorite={}, owner={}", saved.getId(), saved.getFavorite(), userEmail);
        ContactDTO dto = mapper.toDTO(saved, findAppUser(saved.getEmail()));
        publishAction("UPDATE", id, previousJson, toJson(dto), "update", "star", userEmail);
        return dto;
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
                .owner(owner)
                .build();

        repository.save(contact);
        log.info("Contact auto-created: email={}, owner={}", targetEmail, ownerEmail);
    }

    // ─── Helpers ───

    private User findAppUser(String email) {
        if (email == null || email.isBlank()) return null;
        return userQuerySPI.findByEmail(email).orElse(null);
    }
}
