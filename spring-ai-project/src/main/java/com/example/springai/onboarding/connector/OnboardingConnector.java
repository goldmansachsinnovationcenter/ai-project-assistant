package com.example.springai.onboarding.connector;

import com.example.springai.onboarding.entity.ConnectorConfig;
import com.example.springai.onboarding.entity.OnboardingUser;

/**
 * Interface for all onboarding connectors in GSIC-TRACK.
 * Each connector implementation handles onboarding users to a specific platform/service.
 */
public interface OnboardingConnector {

    /**
     * Get the type of this connector.
     */
    ConnectorType getType();

    /**
     * Validate the connector configuration.
     * @param config the connector configuration to validate
     * @return validation result
     */
    ConnectorValidationResult validateConfig(ConnectorConfig config);

    /**
     * Execute onboarding for a user using this connector.
     * @param user the user to onboard
     * @param config the connector configuration
     * @return result of the onboarding execution
     */
    ConnectorExecutionResult executeOnboarding(OnboardingUser user, ConnectorConfig config);

    /**
     * Test the connection using the provided configuration.
     * @param config the connector configuration to test
     * @return result of the connection test
     */
    ConnectorExecutionResult testConnection(ConnectorConfig config);
}
