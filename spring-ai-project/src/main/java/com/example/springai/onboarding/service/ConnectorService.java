package com.example.springai.onboarding.service;

import com.example.springai.onboarding.connector.*;
import com.example.springai.onboarding.entity.ConnectorConfig;
import com.example.springai.onboarding.repository.ConnectorConfigRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Service for managing connector configurations in GSIC-TRACK.
 */
@Service
public class ConnectorService {

    private static final Logger logger = LoggerFactory.getLogger(ConnectorService.class);

    private final ConnectorConfigRepository connectorConfigRepository;
    private final ConnectorRegistry connectorRegistry;

    public ConnectorService(ConnectorConfigRepository connectorConfigRepository, ConnectorRegistry connectorRegistry) {
        this.connectorConfigRepository = connectorConfigRepository;
        this.connectorRegistry = connectorRegistry;
    }

    /**
     * Get all connector configurations.
     */
    public List<ConnectorConfig> getAllConfigs() {
        return connectorConfigRepository.findAll();
    }

    /**
     * Get a connector configuration by ID.
     */
    public Optional<ConnectorConfig> getConfigById(Long id) {
        return connectorConfigRepository.findById(id);
    }

    /**
     * Get all enabled connector configurations.
     */
    public List<ConnectorConfig> getEnabledConfigs() {
        return connectorConfigRepository.findByEnabled(true);
    }

    /**
     * Get connector configurations by type.
     */
    public List<ConnectorConfig> getConfigsByType(ConnectorType type) {
        return connectorConfigRepository.findByConnectorType(type);
    }

    /**
     * Create a new connector configuration.
     */
    public ConnectorConfig createConfig(ConnectorConfig config) {
        logger.info("Creating connector config: {} (type: {})", config.getName(), config.getConnectorType());
        return connectorConfigRepository.save(config);
    }

    /**
     * Update an existing connector configuration.
     */
    public Optional<ConnectorConfig> updateConfig(Long id, ConnectorConfig updatedConfig) {
        return connectorConfigRepository.findById(id).map(existing -> {
            existing.setName(updatedConfig.getName());
            existing.setConnectorType(updatedConfig.getConnectorType());
            existing.setEnabled(updatedConfig.isEnabled());
            existing.setConfigJson(updatedConfig.getConfigJson());
            logger.info("Updating connector config: {} (type: {})", existing.getName(), existing.getConnectorType());
            return connectorConfigRepository.save(existing);
        });
    }

    /**
     * Delete a connector configuration.
     */
    public boolean deleteConfig(Long id) {
        if (connectorConfigRepository.existsById(id)) {
            connectorConfigRepository.deleteById(id);
            logger.info("Deleted connector config with id: {}", id);
            return true;
        }
        return false;
    }

    /**
     * Toggle the enabled status of a connector configuration.
     */
    public Optional<ConnectorConfig> toggleEnabled(Long id) {
        return connectorConfigRepository.findById(id).map(config -> {
            config.setEnabled(!config.isEnabled());
            logger.info("Toggled connector config '{}' enabled: {}", config.getName(), config.isEnabled());
            return connectorConfigRepository.save(config);
        });
    }

    /**
     * Validate a connector configuration using the appropriate connector implementation.
     */
    public ConnectorValidationResult validateConfig(ConnectorConfig config) {
        Optional<OnboardingConnector> connector = connectorRegistry.getConnector(config.getConnectorType());
        if (connector.isEmpty()) {
            return ConnectorValidationResult.failure("No connector implementation found for type: " + config.getConnectorType());
        }
        return connector.get().validateConfig(config);
    }

    /**
     * Test the connection for a connector configuration.
     */
    public ConnectorExecutionResult testConnection(Long configId) {
        Optional<ConnectorConfig> configOpt = connectorConfigRepository.findById(configId);
        if (configOpt.isEmpty()) {
            return ConnectorExecutionResult.failure("Connector configuration not found with id: " + configId);
        }

        ConnectorConfig config = configOpt.get();
        Optional<OnboardingConnector> connector = connectorRegistry.getConnector(config.getConnectorType());
        if (connector.isEmpty()) {
            return ConnectorExecutionResult.failure("No connector implementation found for type: " + config.getConnectorType());
        }

        return connector.get().testConnection(config);
    }

    /**
     * Get all available connector types with their metadata.
     */
    public ConnectorType[] getAvailableConnectorTypes() {
        return ConnectorType.values();
    }
}
