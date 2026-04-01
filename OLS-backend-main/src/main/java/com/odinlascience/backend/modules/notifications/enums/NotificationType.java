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
    SUPPORT_REPLY,

    /** Le statut d'un ticket de support a changé (résolu, fermé) */
    SUPPORT_STATUS_CHANGED,

    /** L'utilisateur a obtenu l'accès à un module payant */
    MODULE_ACCESS_GRANTED,

    /** Une nouvelle connexion a ete detectee sur le compte */
    NEW_LOGIN,

    /** L'utilisateur a ete invite dans une organisation */
    ORGANIZATION_INVITED,

    /** Le role de l'utilisateur a change dans une organisation */
    ORGANIZATION_ROLE_CHANGED,

    /** L'utilisateur a ete retire d'une organisation */
    ORGANIZATION_REMOVED
}
