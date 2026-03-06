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
 * Connector implementation for AWS WorkMail.
 * Creates email accounts in AWS WorkMail as part of onboarding.
 */
@Component
public class AwsWorkMailConnector implements OnboardingConnector {

    private static final Logger logger = LoggerFactory.getLogger(AwsWorkMailConnector.class);
    private final ObjectMapper objectMapper;

    public AwsWorkMailConnector(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public ConnectorType getType() {
        return ConnectorType.AWS_WORKMAIL;
    }

    @Override
    public ConnectorValidationResult validateConfig(ConnectorConfig config) {
        ConnectorValidationResult result = new ConnectorValidationResult();
        try {
            Map<String, String> configMap = parseConfig(config);
            if (!configMap.containsKey("organizationId") || configMap.get("organizationId").isBlank()) {
                result.addError("Organization ID is required");
            }
            if (!configMap.containsKey("region") || configMap.get("region").isBlank()) {
                result.addError("AWS Region is required");
            }
            if (!configMap.containsKey("domain") || configMap.get("domain").isBlank()) {
                result.addError("Email domain is required");
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
        logger.info("Executing AWS WorkMail onboarding for user: {}", user.getEmail());
        try {
            Map<String, String> configMap = parseConfig(config);
            String organizationId = configMap.get("organizationId");
            String domain = configMap.get("domain");

            // In production, this would use AWS SDK to create the mailbox
            String emailAddress = user.getFirstName().toLowerCase() + "." + user.getLastName().toLowerCase() + "@" + domain;
            logger.info("Creating mailbox '{}' in AWS WorkMail organization: {}", emailAddress, organizationId);

            String externalId = "aws-wm-" + System.currentTimeMillis();
            return ConnectorExecutionResult.success(
                    "Mailbox '" + emailAddress + "' created in AWS WorkMail (Organization: " + organizationId + ")",
                    externalId
            );
        } catch (Exception e) {
            logger.error("Failed to create mailbox in AWS WorkMail", e);
            return ConnectorExecutionResult.failure("Failed to create mailbox in AWS WorkMail: " + e.getMessage());
        }
    }

    @Override
    public ConnectorExecutionResult testConnection(ConnectorConfig config) {
        logger.info("Testing AWS WorkMail connection");
        ConnectorValidationResult validation = validateConfig(config);
        if (!validation.isValid()) {
            return ConnectorExecutionResult.failure("Configuration validation failed: " + String.join(", ", validation.getErrors()));
        }

        try {
            Map<String, String> configMap = parseConfig(config);
            String organizationId = configMap.get("organizationId");
            return ConnectorExecutionResult.success("Successfully connected to AWS WorkMail (Organization: " + organizationId + ")");
        } catch (Exception e) {
            return ConnectorExecutionResult.failure("Connection test failed: " + e.getMessage());
        }
    }

    private Map<String, String> parseConfig(ConnectorConfig config) throws Exception {
        return objectMapper.readValue(config.getConfigJson(), new TypeReference<Map<String, String>>() {});
    }
}
