package com.example.springai.onboarding.connector;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class ConnectorValidationResultTest {

    @Test
    void success_ReturnsValidResult() {
        ConnectorValidationResult result = ConnectorValidationResult.success();
        assertTrue(result.isValid());
        assertTrue(result.getErrors().isEmpty());
    }

    @Test
    void failure_WithSingleError_ReturnsInvalidResult() {
        ConnectorValidationResult result = ConnectorValidationResult.failure("Some error");
        assertFalse(result.isValid());
        assertEquals(1, result.getErrors().size());
        assertEquals("Some error", result.getErrors().get(0));
    }

    @Test
    void failure_WithMultipleErrors_ReturnsInvalidResult() {
        ConnectorValidationResult result = ConnectorValidationResult.failure(Arrays.asList("Error 1", "Error 2"));
        assertFalse(result.isValid());
        assertEquals(2, result.getErrors().size());
    }

    @Test
    void addError_MakesResultInvalid() {
        ConnectorValidationResult result = new ConnectorValidationResult();
        assertTrue(result.isValid());

        result.addError("New error");
        assertFalse(result.isValid());
        assertEquals(1, result.getErrors().size());
    }

    @Test
    void settersAndGetters_WorkCorrectly() {
        ConnectorValidationResult result = new ConnectorValidationResult();
        result.setValid(false);
        assertFalse(result.isValid());

        result.setErrors(Arrays.asList("err1", "err2"));
        assertEquals(2, result.getErrors().size());
    }
}
