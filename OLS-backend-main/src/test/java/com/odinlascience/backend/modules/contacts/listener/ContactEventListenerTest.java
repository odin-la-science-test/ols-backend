package com.odinlascience.backend.modules.contacts.listener;

import com.odinlascience.backend.modules.common.event.ShareCreatedEvent;
import com.odinlascience.backend.modules.contacts.service.ContactService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ContactEventListenerTest {

    @Mock
    private ContactService contactService;

    private ContactEventListener listener;

    @BeforeEach
    void setUp() {
        listener = new ContactEventListener(contactService);
    }

    @Test
    void onShareCreated_callsEnsureContactExists() {
        var event = new ShareCreatedEvent(
                "ABC123", "titre", "sender@test.com",
                "Jean Dupont", "recipient@test.com", "/lab/quickshare");

        listener.onShareCreated(event);

        verify(contactService).ensureContactExistsByEmail("sender@test.com", "recipient@test.com");
    }

    @Test
    void onShareCreated_doesNotPropagateException() {
        var event = new ShareCreatedEvent(
                "ABC123", "titre", "sender@test.com",
                "Jean Dupont", "recipient@test.com", "/lab/quickshare");

        doThrow(new RuntimeException("DB error"))
                .when(contactService).ensureContactExistsByEmail(anyString(), anyString());

        listener.onShareCreated(event);

        verify(contactService).ensureContactExistsByEmail("sender@test.com", "recipient@test.com");
    }
}
