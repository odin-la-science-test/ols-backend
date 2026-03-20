package com.odinlascience.backend.modules.contacts.mapper;

import com.odinlascience.backend.modules.contacts.dto.ContactDTO;
import com.odinlascience.backend.modules.contacts.model.Contact;
import com.odinlascience.backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Mapper Contact <-> ContactDTO.
 */
@Component
@RequiredArgsConstructor
public class ContactMapper {

    private final UserRepository userRepository;

    public ContactDTO toDTO(Contact entity) {
        if (entity == null) return null;

        String ownerName = "";
        if (entity.getOwner() != null) {
            ownerName = entity.getOwner().getFirstName() + " " + entity.getOwner().getLastName();
        }

        // Déterminer si l'email du contact correspond à un user OLS inscrit
        boolean isAppUser = false;
        if (entity.getEmail() != null && !entity.getEmail().isBlank()) {
            isAppUser = userRepository.findByEmail(entity.getEmail()).isPresent();
        }

        return ContactDTO.builder()
                .id(entity.getId())
                .firstName(entity.getFirstName())
                .lastName(entity.getLastName())
                .email(entity.getEmail())
                .phone(entity.getPhone())
                .organization(entity.getOrganization())
                .jobTitle(entity.getJobTitle())
                .notes(entity.getNotes())
                .favorite(entity.getFavorite())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .ownerName(ownerName.trim())
                .isAppUser(isAppUser)
                .build();
    }
}
