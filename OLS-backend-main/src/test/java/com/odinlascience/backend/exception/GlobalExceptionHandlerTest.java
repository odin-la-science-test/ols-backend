package com.odinlascience.backend.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.server.ResponseStatusException;

import com.odinlascience.backend.exception.dto.ErrorResponseDTO;

import org.springframework.http.HttpStatus;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.core.MethodParameter;
import org.springframework.validation.BeanPropertyBindingResult;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Path;
import java.util.Set;
import java.util.HashSet;
import java.lang.reflect.Method;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.http.ResponseEntity;

class GlobalExceptionHandlerTest {

    @Test
    void handleNotFoundProducesErrorResponse() {
        GlobalExceptionHandler handler = new GlobalExceptionHandler();
        HttpServletRequest req = mock(HttpServletRequest.class);
        when(req.getRequestURI()).thenReturn("/test/resource");

        ResourceNotFoundException ex = new ResourceNotFoundException("my message");

        var response = handler.handleNotFound(ex, req);

        assertEquals(404, response.getStatusCode().value());
        ErrorResponseDTO body = response.getBody();
        assertNotNull(body);
        assertEquals(404, body.getStatus());
        assertEquals("Ressource Introuvable", body.getError());
        assertEquals("my message", body.getMessage());
        assertEquals("/test/resource", body.getPath());
        assertNotNull(body.getTimestamp());
    }

    @Test
    void handleBadRequestProducesErrorResponse() {
        GlobalExceptionHandler handler = new GlobalExceptionHandler();
        HttpServletRequest req = mock(HttpServletRequest.class);
        when(req.getRequestURI()).thenReturn("/test/bad");

        IllegalArgumentException ex = new IllegalArgumentException("bad param");

        var response = handler.handleBadRequest(ex, req);

        assertEquals(400, response.getStatusCode().value());
        ErrorResponseDTO body = response.getBody();
        assertNotNull(body);
        assertEquals(400, body.getStatus());
        assertEquals("Bad Request", body.getError());
        assertEquals("bad param", body.getMessage());
        assertEquals("/test/bad", body.getPath());
        assertNotNull(body.getTimestamp());
    }

    @Test
    void handleMethodNotAllowedProducesErrorResponse() {
        GlobalExceptionHandler handler = new GlobalExceptionHandler();
        HttpServletRequest req = mock(HttpServletRequest.class);
        when(req.getRequestURI()).thenReturn("/test/method");

        HttpRequestMethodNotSupportedException ex = new HttpRequestMethodNotSupportedException("DELETE");

        var response = handler.handleMethodNotAllowed(ex, req);

        assertEquals(405, response.getStatusCode().value());
        ErrorResponseDTO body = response.getBody();
        assertNotNull(body);
        assertEquals(405, body.getStatus());
        assertEquals("Method Not Allowed", body.getError());
        assertEquals(ex.getMessage(), body.getMessage());
        assertEquals("/test/method", body.getPath());
        assertNotNull(body.getTimestamp());
    }

    @Test
    void handleConflictProducesErrorResponse() {
        GlobalExceptionHandler handler = new GlobalExceptionHandler();
        HttpServletRequest req = mock(HttpServletRequest.class);
        when(req.getRequestURI()).thenReturn("/test/conflict");

        DataIntegrityViolationException ex = new DataIntegrityViolationException("duplicate");

        var response = handler.handleConflict(ex, req);

        assertEquals(409, response.getStatusCode().value());
        ErrorResponseDTO body = response.getBody();
        assertNotNull(body);
        assertEquals(409, body.getStatus());
        assertEquals("Conflict", body.getError());
        assertEquals("duplicate", body.getMessage());
        assertEquals("/test/conflict", body.getPath());
        assertNotNull(body.getTimestamp());
    }

    @Test
    void handleUnsupportedMediaTypeProducesErrorResponse() {
        GlobalExceptionHandler handler = new GlobalExceptionHandler();
        HttpServletRequest req = mock(HttpServletRequest.class);
        when(req.getRequestURI()).thenReturn("/test/media");

        HttpMediaTypeNotSupportedException ex = new HttpMediaTypeNotSupportedException("text/plain");

        var response = handler.handleUnsupportedMediaType(ex, req);

        assertEquals(415, response.getStatusCode().value());
        ErrorResponseDTO body = response.getBody();
        assertNotNull(body);
        assertEquals(415, body.getStatus());
        assertEquals("Unsupported Media Type", body.getError());
        assertEquals(ex.getMessage(), body.getMessage());
        assertEquals("/test/media", body.getPath());
        assertNotNull(body.getTimestamp());
    }

    @Test
    void handleGenericProducesErrorResponse() {
        GlobalExceptionHandler handler = new GlobalExceptionHandler();
        HttpServletRequest req = mock(HttpServletRequest.class);
        when(req.getRequestURI()).thenReturn("/test/generic");

        Exception ex = new Exception("boom");

        var response = handler.handleGeneric(ex, req);

        assertEquals(500, response.getStatusCode().value());
        ErrorResponseDTO body = response.getBody();
        assertNotNull(body);
        assertEquals(500, body.getStatus());
        assertEquals("Internal Server Error", body.getError());
        assertEquals("boom", body.getMessage());
        assertEquals("/test/generic", body.getPath());
        assertNotNull(body.getTimestamp());
    }

    @Test
    void handleValidationProducesErrorResponse() {
        GlobalExceptionHandler handler = new GlobalExceptionHandler();
        HttpServletRequest req = mock(HttpServletRequest.class);
        when(req.getRequestURI()).thenReturn("/test/validation");

        BindException ex = new BindException(new Object(), "obj");
        ex.addError(new FieldError("obj", "name", "must not be empty"));

        ResponseEntity<ErrorResponseDTO> response = handler.handleValidation(ex, req);

        assertEquals(400, response.getStatusCode().value());
        ErrorResponseDTO body = response.getBody();
        assertNotNull(body);
        assertEquals(400, body.getStatus());
        assertEquals("Validation Failed", body.getError());
        assertTrue(body.getMessage().contains("name: must not be empty"));
        assertEquals("/test/validation", body.getPath());
        assertNotNull(body.getTimestamp());
    }

    @SuppressWarnings("unused")
    public void dummyMethod(String param) {}

    @Test
    void handleMethodArgumentNotValidProducesCombinedMessage() throws NoSuchMethodException {
        GlobalExceptionHandler handler = new GlobalExceptionHandler();
        HttpServletRequest req = mock(HttpServletRequest.class);
        when(req.getRequestURI()).thenReturn("/test/method-arg-valid");

        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "obj");
        bindingResult.addError(new FieldError("obj", "first", "first msg"));
        bindingResult.addError(new FieldError("obj", "second", "second msg"));

        Method method = this.getClass().getMethod("dummyMethod", String.class);
        MethodParameter mp = new MethodParameter(method, 0);
        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(mp, bindingResult);

        var response = handler.handleValidation(ex, req);

        assertEquals(400, response.getStatusCode().value());
        ErrorResponseDTO body = response.getBody();
        assertNotNull(body);
        assertEquals(400, body.getStatus());
        assertEquals("Validation Failed", body.getError());
        assertTrue(body.getMessage().contains("first: first msg"));
        assertTrue(body.getMessage().contains("second: second msg"));
        assertEquals("/test/method-arg-valid", body.getPath());
        assertNotNull(body.getTimestamp());
    }

    @Test
    void handleConstraintViolationProducesErrorResponse() {
        GlobalExceptionHandler handler = new GlobalExceptionHandler();
        HttpServletRequest req = mock(HttpServletRequest.class);
        when(req.getRequestURI()).thenReturn("/test/constraint");

        ConstraintViolation<?> cv1 = mock(ConstraintViolation.class);
        ConstraintViolation<?> cv2 = mock(ConstraintViolation.class);
        Path path1 = mock(Path.class);
        Path path2 = mock(Path.class);
        when(path1.toString()).thenReturn("a.field");
        when(path2.toString()).thenReturn("b.field");
        when(cv1.getPropertyPath()).thenReturn(path1);
        when(cv2.getPropertyPath()).thenReturn(path2);
        when(cv1.getMessage()).thenReturn("msg1");
        when(cv2.getMessage()).thenReturn("msg2");

        Set<ConstraintViolation<?>> set = new HashSet<>();
        set.add(cv1);
        set.add(cv2);

        jakarta.validation.ConstraintViolationException ex = new jakarta.validation.ConstraintViolationException(set);

        ResponseEntity<ErrorResponseDTO> response = handler.handleConstraintViolation(ex, req);

        assertEquals(400, response.getStatusCode().value());
        ErrorResponseDTO body = response.getBody();
        assertNotNull(body);
        assertEquals(400, body.getStatus());
        assertEquals("Constraint Violation", body.getError());
        assertTrue(body.getMessage().contains("a.field: msg1") || body.getMessage().contains("b.field: msg2"));
        assertEquals("/test/constraint", body.getPath());
        assertNotNull(body.getTimestamp());
    }

    @Test
    void handleResponseStatusProducesErrorResponse() {
        GlobalExceptionHandler handler = new GlobalExceptionHandler();
        HttpServletRequest req = mock(HttpServletRequest.class);
        when(req.getRequestURI()).thenReturn("/test/response-status");

        ResponseStatusException ex = new ResponseStatusException(HttpStatus.NOT_FOUND, "resource not found");

        var response = handler.handleResponseStatus(ex, req);

        assertEquals(404, response.getStatusCode().value());
        ErrorResponseDTO body = response.getBody();
        assertNotNull(body);
        assertEquals(404, body.getStatus());
        assertTrue(body.getError().contains("404"));
        assertEquals("resource not found", body.getMessage());
        assertEquals("/test/response-status", body.getPath());
        assertNotNull(body.getTimestamp());
    }

    @Test
    void handleOptimisticLockProducesErrorResponse() {
        GlobalExceptionHandler handler = new GlobalExceptionHandler();
        HttpServletRequest req = mock(HttpServletRequest.class);
        when(req.getRequestURI()).thenReturn("/test/optimistic");

        OptimisticLockingFailureException ex = new OptimisticLockingFailureException("optimistic");

        var response = handler.handleOptimisticLock(ex, req);

        assertEquals(409, response.getStatusCode().value());
        ErrorResponseDTO body = response.getBody();
        assertNotNull(body);
        assertEquals(409, body.getStatus());
        assertEquals("Conflict", body.getError());
        assertEquals("optimistic", body.getMessage());
        assertEquals("/test/optimistic", body.getPath());
        assertNotNull(body.getTimestamp());
    }

    @Test
    void handleAccessDeniedProducesErrorResponse() {
        GlobalExceptionHandler handler = new GlobalExceptionHandler();
        HttpServletRequest req = mock(HttpServletRequest.class);
        when(req.getRequestURI()).thenReturn("/test/access-denied");

        org.springframework.security.access.AccessDeniedException ex = 
            new org.springframework.security.access.AccessDeniedException("access denied");

        var response = handler.handleAccessDenied(ex, req);

        assertEquals(403, response.getStatusCode().value());
        ErrorResponseDTO body = response.getBody();
        assertNotNull(body);
        assertEquals(403, body.getStatus());
        assertEquals("Forbidden", body.getError());
        assertEquals("access denied", body.getMessage());
        assertEquals("/test/access-denied", body.getPath());
        assertNotNull(body.getTimestamp());
    }

    @Test
    void handleNoSuchElementProducesErrorResponse() {
        GlobalExceptionHandler handler = new GlobalExceptionHandler();
        HttpServletRequest req = mock(HttpServletRequest.class);
        when(req.getRequestURI()).thenReturn("/test/no-such-element");

        java.util.NoSuchElementException ex = new java.util.NoSuchElementException("element not found");

        var response = handler.handleNoSuchElement(ex, req);

        assertEquals(404, response.getStatusCode().value());
        ErrorResponseDTO body = response.getBody();
        assertNotNull(body);
        assertEquals(404, body.getStatus());
        assertEquals("Ressource Introuvable", body.getError());
        assertEquals("element not found", body.getMessage());
        assertEquals("/test/no-such-element", body.getPath());
        assertNotNull(body.getTimestamp());
    }

    @Test
    void handleAuthenticationProducesErrorResponse() {
        GlobalExceptionHandler handler = new GlobalExceptionHandler();
        HttpServletRequest req = mock(HttpServletRequest.class);
        when(req.getRequestURI()).thenReturn("/test/auth");

        org.springframework.security.core.AuthenticationException ex = 
            new org.springframework.security.authentication.BadCredentialsException("bad credentials");

        var response = handler.handleAuthentication(ex, req);

        assertEquals(401, response.getStatusCode().value());
        ErrorResponseDTO body = response.getBody();
        assertNotNull(body);
        assertEquals(401, body.getStatus());
        assertEquals("Unauthorized", body.getError());
        assertEquals("bad credentials", body.getMessage());
        assertEquals("/test/auth", body.getPath());
        assertNotNull(body.getTimestamp());
    }

}
