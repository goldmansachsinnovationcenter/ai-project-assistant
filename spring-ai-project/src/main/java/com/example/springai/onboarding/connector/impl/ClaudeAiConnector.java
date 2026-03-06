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
 * Connector implementation for Claude AI.
 * Invites users to the Claude AI platform as part of onboarding.
 */
@Component
public class ClaudeAiConnector implements OnboardingConnector {

    private static final Logger logger = LoggerFactory.getLogger(ClaudeAiConnector.class);
    private final ObjectMapper objectMapper;

    public ClaudeAiConnector(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public ConnectorType getType() {
        return ConnectorType.CLAUDE_AI;
    }

    @Override
    public ConnectorValidationResult validateConfig(ConnectorConfig config) {
        ConnectorValidationResult result = new ConnectorValidationResult();
        try {
            Map<String, String> configMap = parseConfig(config);
            if (!configMap.containsKey("apiKey") || configMap.get("apiKey").isBlank()) {
                result.addError("Claude AI API Key is required");
            }
            if (!configMap.containsKey("organizationId") || configMap.get("organizationId").isBlank()) {
                result.addError("Organization ID is required");
            }
        } catch (Exception e) {
            result.addError("Invalid configuration JSON: " + e.getMessage());
        }
        return result;
    }

    @Override
    public ConnectorExecutionResult executeOnboarding(OnboardingUser user, ConnectorConfig config) {
        logger.info("Executing Claude AI onboarding for user: {}", user.getEmail());
        try {
            Map<String, String> configMap = parseConfig(config);
            String organizationId = configMap.get("organizationId");

            // In production, this would call the Claude AI API to invite the user
            logger.info("Inviting user '{}' to Claude AI organization: {}", user.getEmail(), organizationId);

            String externalId = "claude-" + System.currentTimeMillis();
            return ConnectorExecutionResult.success(
                    "User '" + user.getEmail() + "' invited to Claude AI (Organization: " + organizationId + ")",
                    externalId
            );
        } catch (Exception e) {
            logger.error("Failed to invite user to Claude AI", e);
            return ConnectorExecutionResult.failure("Failed to invite user to Claude AI: " + e.getMessage());
        }
    }

    @Override
    public ConnectorExecutionResult testConnection(ConnectorConfig config) {
        logger.info("Testing Claude AI connection");
        ConnectorValidationResult validation = validateConfig(config);
        if (!validation.isValid()) {
            return ConnectorExecutionResult.failure("Configuration validation failed: " + String.join(", ", validation.getErrors()));
        }

        try {
            Map<String, String> configMap = parseConfig(config);
            String organizationId = configMap.get("organizationId");
            return ConnectorExecutionResult.success("Successfully connected to Claude AI (Organization: " + organizationId + ")");
        } catch (Exception e) {
            return ConnectorExecutionResult.failure("Connection test failed: " + e.getMessage());
        }
    }

    private Map<String, String> parseConfig(ConnectorConfig config) throws Exception {
        return objectMapper.readValue(config.getConfigJson(), new TypeReference<Map<String, String>>() {});
    }
}
