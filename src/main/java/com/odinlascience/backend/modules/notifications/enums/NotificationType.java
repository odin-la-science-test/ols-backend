package com.odinlascience.backend.modules.notifications.enums;

/**
 * Types de notifications supportés par le système.
 */
public enum NotificationType {

    /** Un partage QuickShare a été envoyé directement à l'utilisateur */
    QUICKSHARE_RECEIVED,

    /** Un contact a été ajouté (l'utilisateur a été ajouté par quelqu'un) */
    CONTACT_ADDED,

    /** Notification système générique */
    SYSTEM,

    /** Un admin a répondu à un ticket de support */
    SUPPORT_REPLY
}
