package com.example.springai.onboarding.connector;

import java.util.ArrayList;
import java.util.List;

/**
 * Result of validating a connector configuration.
 */
public class ConnectorValidationResult {

    private boolean valid;
    private List<String> errors;

    public ConnectorValidationResult() {
        this.valid = true;
        this.errors = new ArrayList<>();
    }

    public ConnectorValidationResult(boolean valid, List<String> errors) {
        this.valid = valid;
        this.errors = errors != null ? errors : new ArrayList<>();
    }

    public static ConnectorValidationResult success() {
        return new ConnectorValidationResult(true, new ArrayList<>());
    }

    public static ConnectorValidationResult failure(String error) {
        List<String> errors = new ArrayList<>();
        errors.add(error);
        return new ConnectorValidationResult(false, errors);
    }

    public static ConnectorValidationResult failure(List<String> errors) {
        return new ConnectorValidationResult(false, errors);
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }

    public void addError(String error) {
        this.errors.add(error);
        this.valid = false;
    }
}
