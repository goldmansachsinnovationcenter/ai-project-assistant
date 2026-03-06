package com.example.springai.onboarding.connector;

/**
 * Result of executing a connector operation (onboarding or connection test).
 */
public class ConnectorExecutionResult {

    private boolean success;
    private String message;
    private String externalId;

    public ConnectorExecutionResult() {
    }

    public ConnectorExecutionResult(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public ConnectorExecutionResult(boolean success, String message, String externalId) {
        this.success = success;
        this.message = message;
        this.externalId = externalId;
    }

    public static ConnectorExecutionResult success(String message) {
        return new ConnectorExecutionResult(true, message);
    }

    public static ConnectorExecutionResult success(String message, String externalId) {
        return new ConnectorExecutionResult(true, message, externalId);
    }

    public static ConnectorExecutionResult failure(String message) {
        return new ConnectorExecutionResult(false, message);
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }
}
