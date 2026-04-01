package com.odinlascience.backend.security.config;

import com.odinlascience.backend.exception.dto.ErrorResponseDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.access.AccessDeniedException;

import java.io.ByteArrayOutputStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class RestAccessDeniedHandlerTest {

    private RestAccessDeniedHandler handler;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private ByteArrayOutputStream outputStream;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() throws Exception {
        handler = new RestAccessDeniedHandler();
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        outputStream = new ByteArrayOutputStream();
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();

        when(request.getRequestURI()).thenReturn("/api/admin/test");
        when(response.getOutputStream()).thenReturn(new jakarta.servlet.ServletOutputStream() {
            @Override
            public boolean isReady() { return true; }
            @Override
            public void setWriteListener(jakarta.servlet.WriteListener writeListener) {}
            @Override
            public void write(int b) { outputStream.write(b); }
        });
    }

    @Test
    void handle_ReturnsForbiddenResponse() throws Exception {
        AccessDeniedException exception = new AccessDeniedException("Access denied");

        handler.handle(request, response, exception);

        verify(response).setStatus(403);
        verify(response).setContentType("application/json");

        ErrorResponseDTO errorResponse = objectMapper.readValue(outputStream.toByteArray(), ErrorResponseDTO.class);
        assertThat(errorResponse.getStatus()).isEqualTo(403);
        assertThat(errorResponse.getError()).isEqualTo("Forbidden");
        assertThat(errorResponse.getMessage()).isEqualTo("Access denied");
        assertThat(errorResponse.getPath()).isEqualTo("/api/admin/test");
    }
}
