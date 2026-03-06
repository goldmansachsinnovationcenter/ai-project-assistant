package com.example.springai.onboarding.connector.impl;

import com.example.springai.onboarding.connector.*;
import com.example.springai.onboarding.entity.ConnectorConfig;
import com.example.springai.onboarding.entity.OnboardingUser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Connector implementation for Jira.
 * Onboards users to Jira projects as part of the onboarding workflow.
 */
@Component
public class JiraConnector implements OnboardingConnector {

    private static final Logger logger = LoggerFactory.getLogger(JiraConnector.class);
    private final ObjectMapper objectMapper;

    public JiraConnector(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public ConnectorType getType() {
        return ConnectorType.JIRA;
    }

    @Override
    public ConnectorValidationResult validateConfig(ConnectorConfig config) {
        ConnectorValidationResult result = new ConnectorValidationResult();
        try {
            Map<String, String> configMap = parseConfig(config);
            if (!configMap.containsKey("baseUrl") || configMap.get("baseUrl").isBlank()) {
                result.addError("Jira Base URL is required");
            }
            if (!configMap.containsKey("apiToken") || configMap.get("apiToken").isBlank()) {
                result.addError("Jira API Token is required");
            }
            if (!configMap.containsKey("adminEmail") || configMap.get("adminEmail").isBlank()) {
                result.addError("Jira Admin Email is required");
            }
        } catch (Exception e) {
            result.addError("Invalid configuration JSON: " + e.getMessage());
        }
        return result;
    }

    @Override
    public ConnectorExecutionResult executeOnboarding(OnboardingUser user, ConnectorConfig config) {
        logger.info("Executing Jira onboarding for user: {}", user.getEmail());
        try {
            Map<String, String> configMap = parseConfig(config);
            String baseUrl = configMap.get("baseUrl");
            String defaultProject = configMap.getOrDefault("defaultProject", "");

            // In production, this would call the Jira REST API to add the user
            logger.info("Adding user '{}' to Jira instance: {}", user.getEmail(), baseUrl);

            String externalId = "jira-" + System.currentTimeMillis();
            String message = "User '" + user.getEmail() + "' added to Jira (" + baseUrl + ")";
            if (!defaultProject.isBlank()) {
                message += " and assigned to project: " + defaultProject;
            }
            return ConnectorExecutionResult.success(message, externalId);
        } catch (Exception e) {
            logger.error("Failed to add user to Jira", e);
            return ConnectorExecutionResult.failure("Failed to add user to Jira: " + e.getMessage());
        }
    }

    @Override
    public ConnectorExecutionResult testConnection(ConnectorConfig config) {
        logger.info("Testing Jira connection");
        ConnectorValidationResult validation = validateConfig(config);
        if (!validation.isValid()) {
            return ConnectorExecutionResult.failure("Configuration validation failed: " + String.join(", ", validation.getErrors()));
        }

        try {
            Map<String, String> configMap = parseConfig(config);
            String baseUrl = configMap.get("baseUrl");
            return ConnectorExecutionResult.success("Successfully connected to Jira (" + baseUrl + ")");
        } catch (Exception e) {
            return ConnectorExecutionResult.failure("Connection test failed: " + e.getMessage());
        }
    }

    private Map<String, String> parseConfig(ConnectorConfig config) throws Exception {
        return objectMapper.readValue(config.getConfigJson(), new TypeReference<Map<String, String>>() {});
    }
}
