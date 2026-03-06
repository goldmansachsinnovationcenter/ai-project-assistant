package com.example.springai.onboarding.service;

import com.example.springai.onboarding.connector.*;
import com.example.springai.onboarding.entity.ConnectorConfig;
import com.example.springai.onboarding.repository.ConnectorConfigRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConnectorServiceTest {

    @Mock
    private ConnectorConfigRepository connectorConfigRepository;

    @Mock
    private ConnectorRegistry connectorRegistry;

    @InjectMocks
    private ConnectorService connectorService;

    private ConnectorConfig config1;
    private ConnectorConfig config2;

    @BeforeEach
    void setUp() {
        config1 = new ConnectorConfig();
        config1.setId(1L);
        config1.setName("Test JIRA");
        config1.setConnectorType(ConnectorType.JIRA);
        config1.setEnabled(true);
        config1.setConfigJson("{\"baseUrl\":\"https://jira.example.com\",\"apiToken\":\"token\",\"adminEmail\":\"admin@test.com\"}");

        config2 = new ConnectorConfig();
        config2.setId(2L);
        config2.setName("Test GitLab");
        config2.setConnectorType(ConnectorType.GITLAB);
        config2.setEnabled(false);
        config2.setConfigJson("{\"baseUrl\":\"https://gitlab.example.com\",\"privateToken\":\"token\"}");
    }

    @Test
    void getAllConfigs_ReturnsList() {
        when(connectorConfigRepository.findAll()).thenReturn(Arrays.asList(config1, config2));
        List<ConnectorConfig> result = connectorService.getAllConfigs();
        assertEquals(2, result.size());
        verify(connectorConfigRepository).findAll();
    }

    @Test
    void getConfigById_WhenExists_ReturnsConfig() {
        when(connectorConfigRepository.findById(1L)).thenReturn(Optional.of(config1));
        Optional<ConnectorConfig> result = connectorService.getConfigById(1L);
        assertTrue(result.isPresent());
        assertEquals("Test JIRA", result.get().getName());
    }

    @Test
    void getConfigById_WhenNotExists_ReturnsEmpty() {
        when(connectorConfigRepository.findById(999L)).thenReturn(Optional.empty());
        Optional<ConnectorConfig> result = connectorService.getConfigById(999L);
        assertFalse(result.isPresent());
    }

    @Test
    void getEnabledConfigs_ReturnsOnlyEnabled() {
        when(connectorConfigRepository.findByEnabled(true)).thenReturn(Arrays.asList(config1));
        List<ConnectorConfig> result = connectorService.getEnabledConfigs();
        assertEquals(1, result.size());
        assertTrue(result.get(0).isEnabled());
    }

    @Test
    void getConfigsByType_ReturnsList() {
        when(connectorConfigRepository.findByConnectorType(ConnectorType.JIRA)).thenReturn(Arrays.asList(config1));
        List<ConnectorConfig> result = connectorService.getConfigsByType(ConnectorType.JIRA);
        assertEquals(1, result.size());
    }

    @Test
    void createConfig_SavesAndReturns() {
        when(connectorConfigRepository.save(any(ConnectorConfig.class))).thenReturn(config1);
        ConnectorConfig result = connectorService.createConfig(config1);
        assertNotNull(result);
        assertEquals("Test JIRA", result.getName());
        verify(connectorConfigRepository).save(config1);
    }

    @Test
    void updateConfig_WhenExists_Updates() {
        ConnectorConfig updated = new ConnectorConfig();
        updated.setName("Updated JIRA");
        updated.setConnectorType(ConnectorType.JIRA);
        updated.setEnabled(false);
        updated.setConfigJson("{}");

        when(connectorConfigRepository.findById(1L)).thenReturn(Optional.of(config1));
        when(connectorConfigRepository.save(any(ConnectorConfig.class))).thenReturn(config1);

        Optional<ConnectorConfig> result = connectorService.updateConfig(1L, updated);
        assertTrue(result.isPresent());
        verify(connectorConfigRepository).save(any(ConnectorConfig.class));
    }

    @Test
    void updateConfig_WhenNotExists_ReturnsEmpty() {
        when(connectorConfigRepository.findById(999L)).thenReturn(Optional.empty());
        Optional<ConnectorConfig> result = connectorService.updateConfig(999L, config1);
        assertFalse(result.isPresent());
    }

    @Test
    void deleteConfig_WhenExists_ReturnsTrue() {
        when(connectorConfigRepository.existsById(1L)).thenReturn(true);
        assertTrue(connectorService.deleteConfig(1L));
        verify(connectorConfigRepository).deleteById(1L);
    }

    @Test
    void deleteConfig_WhenNotExists_ReturnsFalse() {
        when(connectorConfigRepository.existsById(999L)).thenReturn(false);
        assertFalse(connectorService.deleteConfig(999L));
    }

    @Test
    void toggleEnabled_WhenExists_TogglesAndReturns() {
        when(connectorConfigRepository.findById(1L)).thenReturn(Optional.of(config1));
        when(connectorConfigRepository.save(any(ConnectorConfig.class))).thenAnswer(inv -> inv.getArgument(0));

        Optional<ConnectorConfig> result = connectorService.toggleEnabled(1L);
        assertTrue(result.isPresent());
        assertFalse(result.get().isEnabled()); // was true, toggled to false
    }

    @Test
    void toggleEnabled_WhenNotExists_ReturnsEmpty() {
        when(connectorConfigRepository.findById(999L)).thenReturn(Optional.empty());
        Optional<ConnectorConfig> result = connectorService.toggleEnabled(999L);
        assertFalse(result.isPresent());
    }

    @Test
    void validateConfig_WhenConnectorExists_Validates() {
        OnboardingConnector mockConnector = mock(OnboardingConnector.class);
        when(connectorRegistry.getConnector(ConnectorType.JIRA)).thenReturn(Optional.of(mockConnector));
        when(mockConnector.validateConfig(config1)).thenReturn(ConnectorValidationResult.success());

        ConnectorValidationResult result = connectorService.validateConfig(config1);
        assertTrue(result.isValid());
    }

    @Test
    void validateConfig_WhenConnectorNotExists_ReturnsFailure() {
        when(connectorRegistry.getConnector(ConnectorType.JIRA)).thenReturn(Optional.empty());

        ConnectorValidationResult result = connectorService.validateConfig(config1);
        assertFalse(result.isValid());
    }

    @Test
    void testConnection_WhenConfigExists_Tests() {
        OnboardingConnector mockConnector = mock(OnboardingConnector.class);
        when(connectorConfigRepository.findById(1L)).thenReturn(Optional.of(config1));
        when(connectorRegistry.getConnector(ConnectorType.JIRA)).thenReturn(Optional.of(mockConnector));
        when(mockConnector.testConnection(config1)).thenReturn(ConnectorExecutionResult.success("Connected"));

        ConnectorExecutionResult result = connectorService.testConnection(1L);
        assertTrue(result.isSuccess());
    }

    @Test
    void testConnection_WhenConfigNotExists_ReturnsFailure() {
        when(connectorConfigRepository.findById(999L)).thenReturn(Optional.empty());
        ConnectorExecutionResult result = connectorService.testConnection(999L);
        assertFalse(result.isSuccess());
    }

    @Test
    void testConnection_WhenConnectorNotExists_ReturnsFailure() {
        when(connectorConfigRepository.findById(1L)).thenReturn(Optional.of(config1));
        when(connectorRegistry.getConnector(ConnectorType.JIRA)).thenReturn(Optional.empty());

        ConnectorExecutionResult result = connectorService.testConnection(1L);
        assertFalse(result.isSuccess());
    }

    @Test
    void getAvailableConnectorTypes_ReturnsAllTypes() {
        ConnectorType[] types = connectorService.getAvailableConnectorTypes();
        assertEquals(7, types.length);
    }
}
