package com.odinlascience.backend.user.service;

import com.odinlascience.backend.auth.model.UserSession;
import com.odinlascience.backend.auth.repository.UserSessionRepository;
import com.odinlascience.backend.modules.annotations.model.Annotation;
import com.odinlascience.backend.modules.annotations.repository.AnnotationRepository;
import com.odinlascience.backend.modules.contacts.model.Contact;
import com.odinlascience.backend.modules.contacts.repository.ContactRepository;
import com.odinlascience.backend.modules.history.model.HistoryEntry;
import com.odinlascience.backend.modules.history.repository.HistoryEntryRepository;
import com.odinlascience.backend.modules.notes.model.Note;
import com.odinlascience.backend.modules.notes.repository.NoteRepository;
import com.odinlascience.backend.modules.notifications.model.Notification;
import com.odinlascience.backend.modules.notifications.repository.NotificationRepository;
import com.odinlascience.backend.modules.organization.model.OrganizationMembership;
import com.odinlascience.backend.modules.organization.repository.OrganizationMembershipRepository;
import com.odinlascience.backend.modules.quickshare.model.SharedItem;
import com.odinlascience.backend.modules.quickshare.repository.SharedItemRepository;
import com.odinlascience.backend.modules.studycollections.model.StudyCollection;
import com.odinlascience.backend.modules.studycollections.repository.StudyCollectionRepository;
import com.odinlascience.backend.modules.support.model.SupportTicket;
import com.odinlascience.backend.modules.support.repository.SupportTicketRepository;
import com.odinlascience.backend.user.dto.UserDataExportDTO;
import com.odinlascience.backend.user.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Service d'export des donnees utilisateur (RGPD - droit d'acces).
 * Collecte toutes les donnees personnelles associees a un compte.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserDataExportService {

    private final UserSessionRepository sessionRepository;
    private final ContactRepository contactRepository;
    private final NoteRepository noteRepository;
    private final AnnotationRepository annotationRepository;
    private final NotificationRepository notificationRepository;
    private final HistoryEntryRepository historyEntryRepository;
    private final OrganizationMembershipRepository membershipRepository;
    private final StudyCollectionRepository studyCollectionRepository;
    private final SharedItemRepository sharedItemRepository;
    private final SupportTicketRepository supportTicketRepository;

    @Transactional(readOnly = true)
    public UserDataExportDTO exportUserData(User user) {
        Long userId = user.getId();
        log.info("Export RGPD demande pour userId={}", userId);

        return UserDataExportDTO.builder()
                .exportDate(Instant.now())
                .profile(buildProfile(user))
                .sessions(buildSessions(userId))
                .contacts(buildContacts(userId))
                .notes(buildNotes(userId))
                .annotations(buildAnnotations(userId))
                .notifications(buildNotifications(userId))
                .history(buildHistory(userId))
                .organizations(buildOrganizations(userId))
                .studyCollections(buildStudyCollections(userId))
                .sharedItems(buildSharedItems(userId))
                .supportTickets(buildSupportTickets(userId))
                .build();
    }

    private UserDataExportDTO.ProfileData buildProfile(User user) {
        return UserDataExportDTO.ProfileData.builder()
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .role(user.getRole().name())
                .authProvider(user.getAuthProvider().name())
                .emailVerified(user.isEmailVerified())
                .avatarId(user.getAvatarId())
                .build();
    }

    private List<UserDataExportDTO.SessionData> buildSessions(Long userId) {
        return sessionRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(this::mapSession)
                .toList();
    }

    private UserDataExportDTO.SessionData mapSession(UserSession s) {
        return UserDataExportDTO.SessionData.builder()
                .deviceInfo(s.getDeviceInfo())
                .ipAddress(s.getIpAddress())
                .lastActiveAt(s.getLastActiveAt())
                .createdAt(s.getCreatedAt())
                .build();
    }

    private List<Map<String, Object>> buildContacts(Long userId) {
        return contactRepository
                .findByOwnerIdAndDeletedAtIsNullOrderByFavoriteDescLastNameAscFirstNameAsc(userId)
                .stream().map(this::mapContact).toList();
    }

    private Map<String, Object> mapContact(Contact c) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("firstName", c.getFirstName());
        m.put("lastName", c.getLastName());
        m.put("email", c.getEmail());
        m.put("phone", c.getPhone());
        m.put("organization", c.getOrganization());
        m.put("jobTitle", c.getJobTitle());
        m.put("notes", c.getNotes());
        m.put("favorite", c.getFavorite());
        m.put("createdAt", c.getCreatedAt());
        return m;
    }

    private List<Map<String, Object>> buildNotes(Long userId) {
        return noteRepository
                .findByOwnerIdAndDeletedAtIsNullOrderByPinnedDescUpdatedAtDesc(userId)
                .stream().map(this::mapNote).toList();
    }

    private Map<String, Object> mapNote(Note n) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("title", n.getTitle());
        m.put("content", n.getContent());
        m.put("color", n.getColor());
        m.put("pinned", n.getPinned());
        m.put("tags", n.getTags());
        m.put("createdAt", n.getCreatedAt());
        m.put("updatedAt", n.getUpdatedAt());
        return m;
    }

    private List<Map<String, Object>> buildAnnotations(Long userId) {
        return annotationRepository
                .findByOwnerIdAndDeletedAtIsNullOrderByCreatedAtDesc(userId)
                .stream().map(this::mapAnnotation).toList();
    }

    private Map<String, Object> mapAnnotation(Annotation a) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("entityType", a.getEntityType());
        m.put("entityId", a.getEntityId());
        m.put("content", a.getContent());
        m.put("color", a.getColor());
        m.put("createdAt", a.getCreatedAt());
        return m;
    }

    private List<Map<String, Object>> buildNotifications(Long userId) {
        return notificationRepository.findByRecipientIdOrderByCreatedAtDesc(userId)
                .stream().map(this::mapNotification).toList();
    }

    private Map<String, Object> mapNotification(Notification n) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("type", n.getType());
        m.put("title", n.getTitle());
        m.put("message", n.getMessage());
        m.put("read", n.getRead());
        m.put("createdAt", n.getCreatedAt());
        return m;
    }

    private List<Map<String, Object>> buildHistory(Long userId) {
        return historyEntryRepository.findByOwnerIdOrderByCreatedAtDesc(userId)
                .stream().map(this::mapHistoryEntry).toList();
    }

    private Map<String, Object> mapHistoryEntry(HistoryEntry h) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("moduleSlug", h.getModuleSlug());
        m.put("actionType", h.getActionType());
        m.put("entityId", h.getEntityId());
        m.put("labelKey", h.getLabelKey());
        m.put("createdAt", h.getCreatedAt());
        return m;
    }

    private List<Map<String, Object>> buildOrganizations(Long userId) {
        return membershipRepository
                .findByUserIdAndStatus(userId,
                        com.odinlascience.backend.modules.organization.enums.MembershipStatus.ACTIVE)
                .stream().map(this::mapMembership).toList();
    }

    private Map<String, Object> mapMembership(OrganizationMembership mb) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("organizationName", mb.getOrganization().getName());
        m.put("role", mb.getRole());
        m.put("status", mb.getStatus());
        m.put("joinedAt", mb.getJoinedAt());
        return m;
    }

    private List<Map<String, Object>> buildStudyCollections(Long userId) {
        return studyCollectionRepository
                .findByOwnerIdAndDeletedAtIsNullOrderByCreatedAtDesc(userId)
                .stream().map(this::mapStudyCollection).toList();
    }

    private Map<String, Object> mapStudyCollection(StudyCollection sc) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("name", sc.getName());
        m.put("description", sc.getDescription());
        m.put("itemCount", sc.getItems() != null ? sc.getItems().size() : 0);
        m.put("createdAt", sc.getCreatedAt());
        return m;
    }

    private List<Map<String, Object>> buildSharedItems(Long userId) {
        return sharedItemRepository
                .findByOwnerIdAndDeletedAtIsNullOrderByCreatedAtDesc(userId)
                .stream().map(this::mapSharedItem).toList();
    }

    private Map<String, Object> mapSharedItem(SharedItem si) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("title", si.getTitle());
        m.put("type", si.getType());
        m.put("shareCode", si.getShareCode());
        m.put("createdAt", si.getCreatedAt());
        return m;
    }

    private List<Map<String, Object>> buildSupportTickets(Long userId) {
        return supportTicketRepository
                .findByOwnerIdAndDeletedAtIsNullOrderByCreatedAtDesc(userId)
                .stream().map(this::mapSupportTicket).toList();
    }

    private Map<String, Object> mapSupportTicket(SupportTicket t) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("subject", t.getSubject());
        m.put("description", t.getDescription());
        m.put("category", t.getCategory());
        m.put("priority", t.getPriority());
        m.put("status", t.getStatus());
        m.put("createdAt", t.getCreatedAt());
        return m;
    }
}
