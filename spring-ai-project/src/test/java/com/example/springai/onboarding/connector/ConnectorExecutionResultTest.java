package com.example.springai.onboarding.connector;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ConnectorExecutionResultTest {

    @Test
    void success_WithMessage_ReturnsSuccessResult() {
        ConnectorExecutionResult result = ConnectorExecutionResult.success("Done");
        assertTrue(result.isSuccess());
        assertEquals("Done", result.getMessage());
        assertNull(result.getExternalId());
    }

    @Test
    void success_WithMessageAndExternalId_ReturnsSuccessResult() {
        ConnectorExecutionResult result = ConnectorExecutionResult.success("Done", "ext-123");
        assertTrue(result.isSuccess());
        assertEquals("Done", result.getMessage());
        assertEquals("ext-123", result.getExternalId());
    }

    @Test
    void failure_WithMessage_ReturnsFailureResult() {
        ConnectorExecutionResult result = ConnectorExecutionResult.failure("Error");
        assertFalse(result.isSuccess());
        assertEquals("Error", result.getMessage());
    }

    @Test
    void settersAndGetters_WorkCorrectly() {
        ConnectorExecutionResult result = new ConnectorExecutionResult();
        result.setSuccess(true);
        result.setMessage("test");
        result.setExternalId("ext-1");

        assertTrue(result.isSuccess());
        assertEquals("test", result.getMessage());
        assertEquals("ext-1", result.getExternalId());
    }

    @Test
    void constructor_WithTwoArgs() {
        ConnectorExecutionResult result = new ConnectorExecutionResult(true, "msg");
        assertTrue(result.isSuccess());
        assertEquals("msg", result.getMessage());
        assertNull(result.getExternalId());
    }

    @Test
    void constructor_WithThreeArgs() {
        ConnectorExecutionResult result = new ConnectorExecutionResult(false, "msg", "id");
        assertFalse(result.isSuccess());
        assertEquals("msg", result.getMessage());
        assertEquals("id", result.getExternalId());
    }
}
