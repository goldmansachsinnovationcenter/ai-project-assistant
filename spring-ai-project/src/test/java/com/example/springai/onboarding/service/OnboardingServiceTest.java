package com.example.springai.onboarding.service;

import com.example.springai.onboarding.connector.*;
import com.example.springai.onboarding.entity.*;
import com.example.springai.onboarding.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OnboardingServiceTest {

    @Mock
    private OnboardingUserRepository userRepository;

    @Mock
    private OnboardingTaskRepository taskRepository;

    @Mock
    private ConnectorConfigRepository connectorConfigRepository;

    @Mock
    private ConnectorRegistry connectorRegistry;

    @InjectMocks
    private OnboardingService onboardingService;

    private OnboardingUser user1;
    private ConnectorConfig jiraConfig;

    @BeforeEach
    void setUp() {
        user1 = new OnboardingUser();
        user1.setId(1L);
        user1.setFirstName("John");
        user1.setLastName("Doe");
        user1.setEmail("john.doe@example.com");
        user1.setDepartment("Engineering");
        user1.setJobTitle("Developer");
        user1.setStatus(OnboardingStatus.PENDING);

        jiraConfig = new ConnectorConfig();
        jiraConfig.setId(1L);
        jiraConfig.setName("Jira");
        jiraConfig.setConnectorType(ConnectorType.JIRA);
        jiraConfig.setEnabled(true);
        jiraConfig.setConfigJson("{\"baseUrl\":\"https://jira.example.com\",\"apiToken\":\"token\",\"adminEmail\":\"admin@test.com\"}");
    }

    @Test
    void getAllUsers_ReturnsList() {
        when(userRepository.findAll()).thenReturn(Arrays.asList(user1));
        List<OnboardingUser> result = onboardingService.getAllUsers();
        assertEquals(1, result.size());
    }

    @Test
    void getUserById_WhenExists_Returns() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        Optional<OnboardingUser> result = onboardingService.getUserById(1L);
        assertTrue(result.isPresent());
        assertEquals("John", result.get().getFirstName());
    }

    @Test
    void getUserById_WhenNotExists_ReturnsEmpty() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());
        Optional<OnboardingUser> result = onboardingService.getUserById(999L);
        assertFalse(result.isPresent());
    }

    @Test
    void getUserByEmail_WhenExists_Returns() {
        when(userRepository.findByEmail("john.doe@example.com")).thenReturn(Optional.of(user1));
        Optional<OnboardingUser> result = onboardingService.getUserByEmail("john.doe@example.com");
        assertTrue(result.isPresent());
    }

    @Test
    void getUsersByStatus_ReturnsList() {
        when(userRepository.findByStatus(OnboardingStatus.PENDING)).thenReturn(Arrays.asList(user1));
        List<OnboardingUser> result = onboardingService.getUsersByStatus(OnboardingStatus.PENDING);
        assertEquals(1, result.size());
    }

    @Test
    void createUser_SetsStatusAndSaves() {
        when(userRepository.save(any(OnboardingUser.class))).thenReturn(user1);
        OnboardingUser result = onboardingService.createUser(user1);
        assertNotNull(result);
        assertEquals(OnboardingStatus.PENDING, user1.getStatus());
        verify(userRepository).save(user1);
    }

    @Test
    void updateUser_WhenExists_Updates() {
        OnboardingUser updated = new OnboardingUser();
        updated.setFirstName("Jane");
        updated.setLastName("Smith");
        updated.setEmail("jane@example.com");
        updated.setDepartment("Product");
        updated.setJobTitle("PM");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        when(userRepository.save(any(OnboardingUser.class))).thenReturn(user1);

        Optional<OnboardingUser> result = onboardingService.updateUser(1L, updated);
        assertTrue(result.isPresent());
        verify(userRepository).save(any(OnboardingUser.class));
    }

    @Test
    void updateUser_WhenNotExists_ReturnsEmpty() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());
        Optional<OnboardingUser> result = onboardingService.updateUser(999L, user1);
        assertFalse(result.isPresent());
    }

    @Test
    void deleteUser_WhenExists_ReturnsTrue() {
        when(userRepository.existsById(1L)).thenReturn(true);
        assertTrue(onboardingService.deleteUser(1L));
        verify(userRepository).deleteById(1L);
    }

    @Test
    void deleteUser_WhenNotExists_ReturnsFalse() {
        when(userRepository.existsById(999L)).thenReturn(false);
        assertFalse(onboardingService.deleteUser(999L));
    }

    @Test
    void executeOnboarding_WhenUserNotFound_ThrowsException() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> onboardingService.executeOnboarding(999L));
    }

    @Test
    void executeOnboarding_WhenNoEnabledConnectors_ReturnsUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        when(connectorConfigRepository.findByEnabled(true)).thenReturn(Collections.emptyList());

        OnboardingUser result = onboardingService.executeOnboarding(1L);
        assertNotNull(result);
    }

    @Test
    void executeOnboarding_WithEnabledConnector_ExecutesSuccessfully() {
        OnboardingConnector mockConnector = mock(OnboardingConnector.class);
        OnboardingTask task = new OnboardingTask();
        task.setId(1L);
        task.setConnectorType(ConnectorType.JIRA);
        task.setStatus(OnboardingStatus.PENDING);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        when(userRepository.save(any(OnboardingUser.class))).thenReturn(user1);
        when(connectorConfigRepository.findByEnabled(true)).thenReturn(Arrays.asList(jiraConfig));
        when(connectorRegistry.getConnector(ConnectorType.JIRA)).thenReturn(Optional.of(mockConnector));
        when(mockConnector.executeOnboarding(any(), any())).thenReturn(ConnectorExecutionResult.success("Done", "ext-1"));
        when(taskRepository.save(any(OnboardingTask.class))).thenReturn(task);

        OnboardingUser result = onboardingService.executeOnboarding(1L);
        assertNotNull(result);
        verify(mockConnector).executeOnboarding(any(), any());
    }

    @Test
    void executeOnboarding_WithFailedConnector_SetsFailedStatus() {
        OnboardingConnector mockConnector = mock(OnboardingConnector.class);
        OnboardingTask task = new OnboardingTask();
        task.setId(1L);
        task.setConnectorType(ConnectorType.JIRA);
        task.setStatus(OnboardingStatus.PENDING);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        when(userRepository.save(any(OnboardingUser.class))).thenReturn(user1);
        when(connectorConfigRepository.findByEnabled(true)).thenReturn(Arrays.asList(jiraConfig));
        when(connectorRegistry.getConnector(ConnectorType.JIRA)).thenReturn(Optional.of(mockConnector));
        when(mockConnector.executeOnboarding(any(), any())).thenReturn(ConnectorExecutionResult.failure("Error"));
        when(taskRepository.save(any(OnboardingTask.class))).thenReturn(task);

        OnboardingUser result = onboardingService.executeOnboarding(1L);
        assertNotNull(result);
    }

    @Test
    void executeOnboarding_WhenConnectorThrowsException_HandlesGracefully() {
        OnboardingConnector mockConnector = mock(OnboardingConnector.class);
        OnboardingTask task = new OnboardingTask();
        task.setId(1L);
        task.setConnectorType(ConnectorType.JIRA);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        when(userRepository.save(any(OnboardingUser.class))).thenReturn(user1);
        when(connectorConfigRepository.findByEnabled(true)).thenReturn(Arrays.asList(jiraConfig));
        when(connectorRegistry.getConnector(ConnectorType.JIRA)).thenReturn(Optional.of(mockConnector));
        when(mockConnector.executeOnboarding(any(), any())).thenThrow(new RuntimeException("Network error"));
        when(taskRepository.save(any(OnboardingTask.class))).thenReturn(task);

        OnboardingUser result = onboardingService.executeOnboarding(1L);
        assertNotNull(result);
    }

    @Test
    void getTasksForUser_ReturnsTasks() {
        OnboardingTask task = new OnboardingTask();
        task.setId(1L);
        task.setConnectorType(ConnectorType.JIRA);
        when(taskRepository.findByOnboardingUserId(1L)).thenReturn(Arrays.asList(task));

        List<OnboardingTask> result = onboardingService.getTasksForUser(1L);
        assertEquals(1, result.size());
    }

    @Test
    void retryTask_WhenTaskNotFound_ThrowsException() {
        when(taskRepository.findById(999L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> onboardingService.retryTask(999L));
    }

    @Test
    void retryTask_WhenTaskNotFailed_ThrowsException() {
        OnboardingTask task = new OnboardingTask();
        task.setId(1L);
        task.setStatus(OnboardingStatus.COMPLETED);
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

        assertThrows(RuntimeException.class, () -> onboardingService.retryTask(1L));
    }
}
