package com.odinlascience.backend.user.service;

import com.odinlascience.backend.auth.service.SessionService;
import com.odinlascience.backend.modules.annotations.repository.AnnotationRepository;
import com.odinlascience.backend.modules.contacts.repository.ContactRepository;
import com.odinlascience.backend.modules.notes.repository.NoteRepository;
import com.odinlascience.backend.modules.notifications.repository.NotificationRepository;
import com.odinlascience.backend.modules.organization.repository.OrganizationMembershipRepository;
import com.odinlascience.backend.modules.quickshare.repository.SharedItemRepository;
import com.odinlascience.backend.modules.studycollections.repository.StudyCollectionRepository;
import com.odinlascience.backend.modules.support.repository.SupportTicketRepository;
import com.odinlascience.backend.user.enums.AuthProvider;
import com.odinlascience.backend.user.model.User;
import com.odinlascience.backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

/**
 * Service de suppression / anonymisation de compte (RGPD - droit a l'effacement).
 * Anonymise le profil et supprime les donnees personnelles associees.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserDataDeletionService {

    private final UserRepository userRepository;
    private final SessionService sessionService;
    private final ContactRepository contactRepository;
    private final NoteRepository noteRepository;
    private final AnnotationRepository annotationRepository;
    private final NotificationRepository notificationRepository;
    private final OrganizationMembershipRepository membershipRepository;
    private final StudyCollectionRepository studyCollectionRepository;
    private final SharedItemRepository sharedItemRepository;
    private final SupportTicketRepository supportTicketRepository;

    @Transactional
    public void deleteAccount(User user, String confirmEmail) {
        if (!user.getEmail().equalsIgnoreCase(confirmEmail)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "L'email de confirmation ne correspond pas au compte");
        }

        Long userId = user.getId();
        log.info("Suppression de compte RGPD demandee pour userId={}", userId);

        deleteUserData(userId);
        anonymizeUser(user);

        log.info("Compte anonymise avec succes pour userId={}", userId);
    }

    private void deleteUserData(Long userId) {
        sessionService.revokeAllSessions(userId);
        contactRepository.deleteByOwnerId(userId);
        noteRepository.deleteByOwnerId(userId);
        annotationRepository.deleteByOwnerId(userId);
        notificationRepository.deleteByRecipientId(userId);
        studyCollectionRepository.deleteByOwnerId(userId);
        sharedItemRepository.deleteByOwnerId(userId);
        supportTicketRepository.deleteByOwnerId(userId);
        membershipRepository.deleteByUserId(userId);
    }

    private void anonymizeUser(User user) {
        user.setEmail("deleted-" + user.getId() + "@deleted.ols");
        user.setFirstName("Utilisateur");
        user.setLastName("Supprime");
        user.setPassword(null);
        user.setAuthProvider(AuthProvider.LOCAL);
        user.setExternalId(null);
        user.setAvatarId(null);
        user.setEmailVerified(false);
        userRepository.save(user);
    }
}
