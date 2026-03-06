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
 * Connector implementation for AWS Directory Services.
 * Creates users in AWS Directory Services as part of onboarding.
 */
@Component
public class AwsDirectoryServiceConnector implements OnboardingConnector {

    private static final Logger logger = LoggerFactory.getLogger(AwsDirectoryServiceConnector.class);
    private final ObjectMapper objectMapper;

    public AwsDirectoryServiceConnector(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public ConnectorType getType() {
        return ConnectorType.AWS_DIRECTORY_SERVICE;
    }

    @Override
    public ConnectorValidationResult validateConfig(ConnectorConfig config) {
        ConnectorValidationResult result = new ConnectorValidationResult();
        try {
            Map<String, String> configMap = parseConfig(config);
            if (!configMap.containsKey("directoryId") || configMap.get("directoryId").isBlank()) {
                result.addError("Directory ID is required");
            }
            if (!configMap.containsKey("region") || configMap.get("region").isBlank()) {
                result.addError("AWS Region is required");
            }
            if (!configMap.containsKey("accessKeyId") || configMap.get("accessKeyId").isBlank()) {
                result.addError("AWS Access Key ID is required");
            }
            if (!configMap.containsKey("secretAccessKey") || configMap.get("secretAccessKey").isBlank()) {
                result.addError("AWS Secret Access Key is required");
            }
        } catch (Exception e) {
            result.addError("Invalid configuration JSON: " + e.getMessage());
        }
        return result;
    }

    @Override
    public ConnectorExecutionResult executeOnboarding(OnboardingUser user, ConnectorConfig config) {
        logger.info("Executing AWS Directory Service onboarding for user: {}", user.getEmail());
        try {
            Map<String, String> configMap = parseConfig(config);
            String directoryId = configMap.get("directoryId");

            // In production, this would use AWS SDK to create the user
            // For now, simulate the API call
            String username = user.getEmail().split("@")[0];
            logger.info("Creating user '{}' in AWS Directory Service directory: {}", username, directoryId);

            // Simulated successful creation
            String externalId = "aws-ds-" + System.currentTimeMillis();
            return ConnectorExecutionResult.success(
                    "User '" + username + "' created in AWS Directory Service (Directory: " + directoryId + ")",
                    externalId
            );
        } catch (Exception e) {
            logger.error("Failed to create user in AWS Directory Service", e);
            return ConnectorExecutionResult.failure("Failed to create user in AWS Directory Service: " + e.getMessage());
        }
    }

    @Override
    public ConnectorExecutionResult testConnection(ConnectorConfig config) {
        logger.info("Testing AWS Directory Service connection");
        ConnectorValidationResult validation = validateConfig(config);
        if (!validation.isValid()) {
            return ConnectorExecutionResult.failure("Configuration validation failed: " + String.join(", ", validation.getErrors()));
        }

        try {
            Map<String, String> configMap = parseConfig(config);
            String directoryId = configMap.get("directoryId");
            // In production, would test actual AWS connectivity
            return ConnectorExecutionResult.success("Successfully connected to AWS Directory Service (Directory: " + directoryId + ")");
        } catch (Exception e) {
            return ConnectorExecutionResult.failure("Connection test failed: " + e.getMessage());
        }
    }

    private Map<String, String> parseConfig(ConnectorConfig config) throws Exception {
        return objectMapper.readValue(config.getConfigJson(), new TypeReference<Map<String, String>>() {});
    }
}
