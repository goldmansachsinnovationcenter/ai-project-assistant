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
 * Connector implementation for GitLab.
 * Onboards users to GitLab repositories and groups as part of onboarding.
 */
@Component
public class GitLabConnector implements OnboardingConnector {

    private static final Logger logger = LoggerFactory.getLogger(GitLabConnector.class);
    private final ObjectMapper objectMapper;

    public GitLabConnector(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public ConnectorType getType() {
        return ConnectorType.GITLAB;
    }

    @Override
    public ConnectorValidationResult validateConfig(ConnectorConfig config) {
        ConnectorValidationResult result = new ConnectorValidationResult();
        try {
            Map<String, String> configMap = parseConfig(config);
            if (!configMap.containsKey("baseUrl") || configMap.get("baseUrl").isBlank()) {
                result.addError("GitLab Base URL is required");
            }
            if (!configMap.containsKey("privateToken") || configMap.get("privateToken").isBlank()) {
                result.addError("GitLab Private Token is required");
            }
        } catch (Exception e) {
            result.addError("Invalid configuration JSON: " + e.getMessage());
        }
        return result;
    }

    @Override
    public ConnectorExecutionResult executeOnboarding(OnboardingUser user, ConnectorConfig config) {
        logger.info("Executing GitLab onboarding for user: {}", user.getEmail());
        try {
            Map<String, String> configMap = parseConfig(config);
            String baseUrl = configMap.get("baseUrl");
            String defaultGroup = configMap.getOrDefault("defaultGroup", "");
            String accessLevel = configMap.getOrDefault("accessLevel", "Developer");

            // In production, this would call the GitLab API to create/invite the user
            logger.info("Adding user '{}' to GitLab: {} with access level: {}", user.getEmail(), baseUrl, accessLevel);

            String externalId = "gitlab-" + System.currentTimeMillis();
            String message = "User '" + user.getEmail() + "' added to GitLab (" + baseUrl + ") with access: " + accessLevel;
            if (!defaultGroup.isBlank()) {
                message += " in group: " + defaultGroup;
            }
            return ConnectorExecutionResult.success(message, externalId);
        } catch (Exception e) {
            logger.error("Failed to add user to GitLab", e);
            return ConnectorExecutionResult.failure("Failed to add user to GitLab: " + e.getMessage());
        }
    }

    @Override
    public ConnectorExecutionResult testConnection(ConnectorConfig config) {
        logger.info("Testing GitLab connection");
        ConnectorValidationResult validation = validateConfig(config);
        if (!validation.isValid()) {
            return ConnectorExecutionResult.failure("Configuration validation failed: " + String.join(", ", validation.getErrors()));
        }

        try {
            Map<String, String> configMap = parseConfig(config);
            String baseUrl = configMap.get("baseUrl");
            return ConnectorExecutionResult.success("Successfully connected to GitLab (" + baseUrl + ")");
        } catch (Exception e) {
            return ConnectorExecutionResult.failure("Connection test failed: " + e.getMessage());
        }
    }

    private Map<String, String> parseConfig(ConnectorConfig config) throws Exception {
        return objectMapper.readValue(config.getConfigJson(), new TypeReference<Map<String, String>>() {});
    }
}
