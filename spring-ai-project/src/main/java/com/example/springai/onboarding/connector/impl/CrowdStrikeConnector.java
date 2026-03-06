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
 * Connector implementation for CrowdStrike.
 * Onboards users to CrowdStrike endpoint security platform as part of onboarding.
 */
@Component
public class CrowdStrikeConnector implements OnboardingConnector {

    private static final Logger logger = LoggerFactory.getLogger(CrowdStrikeConnector.class);
    private final ObjectMapper objectMapper;

    public CrowdStrikeConnector(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public ConnectorType getType() {
        return ConnectorType.CROWDSTRIKE;
    }

    @Override
    public ConnectorValidationResult validateConfig(ConnectorConfig config) {
        ConnectorValidationResult result = new ConnectorValidationResult();
        try {
            Map<String, String> configMap = parseConfig(config);
            if (!configMap.containsKey("clientId") || configMap.get("clientId").isBlank()) {
                result.addError("CrowdStrike Client ID is required");
            }
            if (!configMap.containsKey("clientSecret") || configMap.get("clientSecret").isBlank()) {
                result.addError("CrowdStrike Client Secret is required");
            }
            if (!configMap.containsKey("baseUrl") || configMap.get("baseUrl").isBlank()) {
                result.addError("CrowdStrike Base URL is required");
            }
        } catch (Exception e) {
            result.addError("Invalid configuration JSON: " + e.getMessage());
        }
        return result;
    }

    @Override
    public ConnectorExecutionResult executeOnboarding(OnboardingUser user, ConnectorConfig config) {
        logger.info("Executing CrowdStrike onboarding for user: {}", user.getEmail());
        try {
            Map<String, String> configMap = parseConfig(config);
            String baseUrl = configMap.get("baseUrl");
            String defaultRole = configMap.getOrDefault("defaultRole", "Read Only Analyst");

            // In production, this would call the CrowdStrike Falcon API to create the user
            logger.info("Adding user '{}' to CrowdStrike: {} with role: {}", user.getEmail(), baseUrl, defaultRole);

            String externalId = "cs-" + System.currentTimeMillis();
            return ConnectorExecutionResult.success(
                    "User '" + user.getEmail() + "' added to CrowdStrike (" + baseUrl + ") with role: " + defaultRole,
                    externalId
            );
        } catch (Exception e) {
            logger.error("Failed to add user to CrowdStrike", e);
            return ConnectorExecutionResult.failure("Failed to add user to CrowdStrike: " + e.getMessage());
        }
    }

    @Override
    public ConnectorExecutionResult testConnection(ConnectorConfig config) {
        logger.info("Testing CrowdStrike connection");
        ConnectorValidationResult validation = validateConfig(config);
        if (!validation.isValid()) {
            return ConnectorExecutionResult.failure("Configuration validation failed: " + String.join(", ", validation.getErrors()));
        }

        try {
            Map<String, String> configMap = parseConfig(config);
            String baseUrl = configMap.get("baseUrl");
            return ConnectorExecutionResult.success("Successfully connected to CrowdStrike (" + baseUrl + ")");
        } catch (Exception e) {
            return ConnectorExecutionResult.failure("Connection test failed: " + e.getMessage());
        }
    }

    private Map<String, String> parseConfig(ConnectorConfig config) throws Exception {
        return objectMapper.readValue(config.getConfigJson(), new TypeReference<Map<String, String>>() {});
    }
}
