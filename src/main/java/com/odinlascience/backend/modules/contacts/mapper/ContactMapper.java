package com.odinlascience.backend.modules.contacts.mapper;

import com.odinlascience.backend.modules.contacts.dto.ContactDTO;
import com.odinlascience.backend.modules.contacts.model.Contact;
import com.odinlascience.backend.user.model.User;
import org.springframework.stereotype.Component;

/**
 * Mapper Contact <-> ContactDTO.
 */
@Component
public class ContactMapper {

    public ContactDTO toDTO(Contact entity, User appUser) {
        if (entity == null) return null;

        String ownerName = entity.getOwner() != null ? entity.getOwner().getFullName() : "";

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
                .ownerName(ownerName)
                .isAppUser(appUser != null)
                .appUserId(appUser != null ? appUser.getId() : null)
                .appUserAvatarId(appUser != null ? appUser.getAvatarId() : null)
                .build();
    }
}
