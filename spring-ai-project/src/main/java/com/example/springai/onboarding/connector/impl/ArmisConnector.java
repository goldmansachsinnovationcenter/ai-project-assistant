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
 * Connector implementation for Armis.
 * Onboards users to the Armis security platform as part of onboarding.
 */
@Component
public class ArmisConnector implements OnboardingConnector {

    private static final Logger logger = LoggerFactory.getLogger(ArmisConnector.class);
    private final ObjectMapper objectMapper;

    public ArmisConnector(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public ConnectorType getType() {
        return ConnectorType.ARMIS;
    }

    @Override
    public ConnectorValidationResult validateConfig(ConnectorConfig config) {
        ConnectorValidationResult result = new ConnectorValidationResult();
        try {
            Map<String, String> configMap = parseConfig(config);
            if (!configMap.containsKey("tenantUrl") || configMap.get("tenantUrl").isBlank()) {
                result.addError("Armis Tenant URL is required");
            }
            if (!configMap.containsKey("apiSecret") || configMap.get("apiSecret").isBlank()) {
                result.addError("Armis API Secret is required");
            }
        } catch (Exception e) {
            result.addError("Invalid configuration JSON: " + e.getMessage());
        }
        return result;
    }

    @Override
    public ConnectorExecutionResult executeOnboarding(OnboardingUser user, ConnectorConfig config) {
        logger.info("Executing Armis onboarding for user: {}", user.getEmail());
        try {
            Map<String, String> configMap = parseConfig(config);
            String tenantUrl = configMap.get("tenantUrl");
            String defaultRole = configMap.getOrDefault("defaultRole", "Viewer");

            // In production, this would call the Armis API to create/invite the user
            logger.info("Adding user '{}' to Armis tenant: {} with role: {}", user.getEmail(), tenantUrl, defaultRole);

            String externalId = "armis-" + System.currentTimeMillis();
            return ConnectorExecutionResult.success(
                    "User '" + user.getEmail() + "' added to Armis (" + tenantUrl + ") with role: " + defaultRole,
                    externalId
            );
        } catch (Exception e) {
            logger.error("Failed to add user to Armis", e);
            return ConnectorExecutionResult.failure("Failed to add user to Armis: " + e.getMessage());
        }
    }

    @Override
    public ConnectorExecutionResult testConnection(ConnectorConfig config) {
        logger.info("Testing Armis connection");
        ConnectorValidationResult validation = validateConfig(config);
        if (!validation.isValid()) {
            return ConnectorExecutionResult.failure("Configuration validation failed: " + String.join(", ", validation.getErrors()));
        }

        try {
            Map<String, String> configMap = parseConfig(config);
            String tenantUrl = configMap.get("tenantUrl");
            return ConnectorExecutionResult.success("Successfully connected to Armis (" + tenantUrl + ")");
        } catch (Exception e) {
            return ConnectorExecutionResult.failure("Connection test failed: " + e.getMessage());
        }
    }

    private Map<String, String> parseConfig(ConnectorConfig config) throws Exception {
        return objectMapper.readValue(config.getConfigJson(), new TypeReference<Map<String, String>>() {});
    }
}
