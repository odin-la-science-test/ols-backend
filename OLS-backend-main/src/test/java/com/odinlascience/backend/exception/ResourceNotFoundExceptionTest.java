package com.odinlascience.backend.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ResourceNotFoundExceptionTest {

    @Test
    void messageIsStored() {
        String msg = "not found";
        ResourceNotFoundException ex = new ResourceNotFoundException(msg);
        assertEquals(msg, ex.getMessage());
    }

}
