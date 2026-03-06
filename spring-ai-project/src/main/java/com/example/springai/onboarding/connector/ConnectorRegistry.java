package com.example.springai.onboarding.connector;

import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Registry that manages all available onboarding connectors in GSIC-TRACK.
 * Connectors are auto-discovered via Spring dependency injection.
 */
@Component
public class ConnectorRegistry {

    private final Map<ConnectorType, OnboardingConnector> connectors;

    public ConnectorRegistry(List<OnboardingConnector> connectorList) {
        this.connectors = new EnumMap<>(ConnectorType.class);
        for (OnboardingConnector connector : connectorList) {
            this.connectors.put(connector.getType(), connector);
        }
    }

    /**
     * Get a connector by its type.
     */
    public Optional<OnboardingConnector> getConnector(ConnectorType type) {
        return Optional.ofNullable(connectors.get(type));
    }

    /**
     * Get all registered connectors.
     */
    public Map<ConnectorType, OnboardingConnector> getAllConnectors() {
        return Collections.unmodifiableMap(connectors);
    }

    /**
     * Check if a connector type is registered.
     */
    public boolean hasConnector(ConnectorType type) {
        return connectors.containsKey(type);
    }
}
