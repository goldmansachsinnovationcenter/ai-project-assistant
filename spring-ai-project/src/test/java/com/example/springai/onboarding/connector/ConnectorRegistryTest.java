package com.example.springai.onboarding.connector;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ConnectorRegistryTest {

    private OnboardingConnector mockConnector1;
    private OnboardingConnector mockConnector2;

    @BeforeEach
    void setUp() {
        mockConnector1 = mock(OnboardingConnector.class);
        when(mockConnector1.getType()).thenReturn(ConnectorType.JIRA);

        mockConnector2 = mock(OnboardingConnector.class);
        when(mockConnector2.getType()).thenReturn(ConnectorType.GITLAB);
    }

    @Test
    void getConnector_WhenRegistered_ReturnsConnector() {
        ConnectorRegistry registry = new ConnectorRegistry(Arrays.asList(mockConnector1, mockConnector2));

        Optional<OnboardingConnector> result = registry.getConnector(ConnectorType.JIRA);
        assertTrue(result.isPresent());
        assertEquals(ConnectorType.JIRA, result.get().getType());
    }

    @Test
    void getConnector_WhenNotRegistered_ReturnsEmpty() {
        ConnectorRegistry registry = new ConnectorRegistry(Arrays.asList(mockConnector1));

        Optional<OnboardingConnector> result = registry.getConnector(ConnectorType.ARMIS);
        assertFalse(result.isPresent());
    }

    @Test
    void getAllConnectors_ReturnsAll() {
        ConnectorRegistry registry = new ConnectorRegistry(Arrays.asList(mockConnector1, mockConnector2));

        Map<ConnectorType, OnboardingConnector> all = registry.getAllConnectors();
        assertEquals(2, all.size());
        assertTrue(all.containsKey(ConnectorType.JIRA));
        assertTrue(all.containsKey(ConnectorType.GITLAB));
    }

    @Test
    void getAllConnectors_EmptyList_ReturnsEmptyMap() {
        ConnectorRegistry registry = new ConnectorRegistry(Collections.emptyList());

        Map<ConnectorType, OnboardingConnector> all = registry.getAllConnectors();
        assertTrue(all.isEmpty());
    }

    @Test
    void hasConnector_WhenRegistered_ReturnsTrue() {
        ConnectorRegistry registry = new ConnectorRegistry(Arrays.asList(mockConnector1));
        assertTrue(registry.hasConnector(ConnectorType.JIRA));
    }

    @Test
    void hasConnector_WhenNotRegistered_ReturnsFalse() {
        ConnectorRegistry registry = new ConnectorRegistry(Arrays.asList(mockConnector1));
        assertFalse(registry.hasConnector(ConnectorType.CROWDSTRIKE));
    }
}
