package com.odinlascience.backend.modules.notifications.listener;

import com.odinlascience.backend.modules.common.event.ModuleAccessGrantedEvent;
import com.odinlascience.backend.modules.common.event.ShareCreatedEvent;
import com.odinlascience.backend.modules.common.event.TicketRepliedEvent;
import com.odinlascience.backend.modules.common.event.TicketStatusChangedEvent;
import com.odinlascience.backend.modules.notifications.enums.NotificationType;
import com.odinlascience.backend.modules.notifications.service.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationEventListenerTest {

    @Mock
    private NotificationService notificationService;

    private NotificationEventListener listener;

    @BeforeEach
    void setUp() {
        listener = new NotificationEventListener(notificationService);
    }

    @Test
    void onShareCreated_sendsNotification() {
        var event = new ShareCreatedEvent(
                "ABC123", "Mon partage", "sender@test.com",
                "Jean Dupont", "recipient@test.com", "/lab/quickshare");

        listener.onShareCreated(event);

        verify(notificationService).send(
                eq("recipient@test.com"),
                eq(NotificationType.QUICKSHARE_RECEIVED),
                contains("Jean Dupont"),
                contains("ABC123"),
                eq("/lab/quickshare"),
                contains("ABC123"));
    }

    @Test
    void onShareCreated_usesEmailWhenFullNameBlank() {
        var event = new ShareCreatedEvent(
                "ABC123", "Mon partage", "sender@test.com",
                "", "recipient@test.com", "/lab/quickshare");

        listener.onShareCreated(event);

        verify(notificationService).send(
                eq("recipient@test.com"),
                eq(NotificationType.QUICKSHARE_RECEIVED),
                contains("sender@test.com"),
                anyString(), anyString(), anyString());
    }

    @Test
    void onShareCreated_doesNotPropagateException() {
        var event = new ShareCreatedEvent(
                "ABC123", "titre", "sender@test.com",
                "Jean", "recipient@test.com", "/lab/quickshare");

        when(notificationService.send(anyString(), any(), anyString(), anyString(), anyString(), anyString()))
                .thenThrow(new RuntimeException("DB error"));

        listener.onShareCreated(event);

        verify(notificationService).send(anyString(), any(), anyString(), anyString(), anyString(), anyString());
    }

    @Test
    void onTicketReplied_sendsNotification() {
        var event = new TicketRepliedEvent(42L, "Bug critique", "user@test.com", "/lab/support");

        listener.onTicketReplied(event);

        verify(notificationService).send(
                eq("user@test.com"),
                eq(NotificationType.SUPPORT_REPLY),
                contains("#42"),
                contains("Bug critique"),
                eq("/lab/support"),
                contains("42"));
    }

    @Test
    void onTicketReplied_doesNotPropagateException() {
        var event = new TicketRepliedEvent(1L, "Test", "user@test.com", "/lab/support");

        when(notificationService.send(anyString(), any(), anyString(), anyString(), anyString(), anyString()))
                .thenThrow(new RuntimeException("DB error"));

        listener.onTicketReplied(event);

        verify(notificationService).send(anyString(), any(), anyString(), anyString(), anyString(), anyString());
    }

    @Test
    void onTicketStatusChanged_sendsNotification() {
        var event = new TicketStatusChangedEvent(42L, "Bug critique", "user@test.com", "RESOLVED", "/lab/support");

        listener.onTicketStatusChanged(event);

        verify(notificationService).send(
                eq("user@test.com"),
                eq(NotificationType.SUPPORT_STATUS_CHANGED),
                contains("#42"),
                contains("RESOLVED"),
                eq("/lab/support"),
                contains("42"));
    }

    @Test
    void onTicketStatusChanged_doesNotPropagateException() {
        var event = new TicketStatusChangedEvent(1L, "Test", "user@test.com", "CLOSED", "/lab/support");

        when(notificationService.send(anyString(), any(), anyString(), anyString(), anyString(), anyString()))
                .thenThrow(new RuntimeException("DB error"));

        listener.onTicketStatusChanged(event);

        verify(notificationService).send(anyString(), any(), anyString(), anyString(), anyString(), anyString());
    }

    @Test
    void onModuleAccessGranted_sendsNotification() {
        var event = new ModuleAccessGrantedEvent("user@test.com", "mycology", "Mycologie", "/mycology");

        listener.onModuleAccessGranted(event);

        verify(notificationService).send(
                eq("user@test.com"),
                eq(NotificationType.MODULE_ACCESS_GRANTED),
                contains("Mycologie"),
                contains("Mycologie"),
                eq("/mycology"),
                contains("mycology"));
    }

    @Test
    void onModuleAccessGranted_doesNotPropagateException() {
        var event = new ModuleAccessGrantedEvent("user@test.com", "mycology", "Mycologie", "/mycology");

        when(notificationService.send(anyString(), any(), anyString(), anyString(), anyString(), anyString()))
                .thenThrow(new RuntimeException("DB error"));

        listener.onModuleAccessGranted(event);

        verify(notificationService).send(anyString(), any(), anyString(), anyString(), anyString(), anyString());
    }
}
