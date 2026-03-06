package com.example.springai.onboarding.service;

import com.example.springai.onboarding.connector.*;
import com.example.springai.onboarding.entity.*;
import com.example.springai.onboarding.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service for managing user onboarding workflows in GSIC-TRACK.
 */
@Service
public class OnboardingService {

    private static final Logger logger = LoggerFactory.getLogger(OnboardingService.class);

    private final OnboardingUserRepository userRepository;
    private final OnboardingTaskRepository taskRepository;
    private final ConnectorConfigRepository connectorConfigRepository;
    private final ConnectorRegistry connectorRegistry;

    public OnboardingService(
            OnboardingUserRepository userRepository,
            OnboardingTaskRepository taskRepository,
            ConnectorConfigRepository connectorConfigRepository,
            ConnectorRegistry connectorRegistry) {
        this.userRepository = userRepository;
        this.taskRepository = taskRepository;
        this.connectorConfigRepository = connectorConfigRepository;
        this.connectorRegistry = connectorRegistry;
    }

    /**
     * Get all onboarding users.
     */
    public List<OnboardingUser> getAllUsers() {
        return userRepository.findAll();
    }

    /**
     * Get an onboarding user by ID.
     */
    public Optional<OnboardingUser> getUserById(Long id) {
        return userRepository.findById(id);
    }

    /**
     * Get an onboarding user by email.
     */
    public Optional<OnboardingUser> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    /**
     * Get onboarding users by status.
     */
    public List<OnboardingUser> getUsersByStatus(OnboardingStatus status) {
        return userRepository.findByStatus(status);
    }

    /**
     * Create a new onboarding user.
     */
    public OnboardingUser createUser(OnboardingUser user) {
        logger.info("Creating onboarding user: {} ({})", user.getFullName(), user.getEmail());
        user.setStatus(OnboardingStatus.PENDING);
        return userRepository.save(user);
    }

    /**
     * Update an existing onboarding user.
     */
    public Optional<OnboardingUser> updateUser(Long id, OnboardingUser updatedUser) {
        return userRepository.findById(id).map(existing -> {
            existing.setFirstName(updatedUser.getFirstName());
            existing.setLastName(updatedUser.getLastName());
            existing.setEmail(updatedUser.getEmail());
            existing.setDepartment(updatedUser.getDepartment());
            existing.setJobTitle(updatedUser.getJobTitle());
            return userRepository.save(existing);
        });
    }

    /**
     * Delete an onboarding user.
     */
    public boolean deleteUser(Long id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            logger.info("Deleted onboarding user with id: {}", id);
            return true;
        }
        return false;
    }

    /**
     * Execute onboarding for a user across all enabled connectors.
     */
    @Transactional
    public OnboardingUser executeOnboarding(Long userId) {
        OnboardingUser user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        List<ConnectorConfig> enabledConfigs = connectorConfigRepository.findByEnabled(true);
        if (enabledConfigs.isEmpty()) {
            logger.warn("No enabled connectors found for onboarding user: {}", user.getEmail());
            return user;
        }

        user.setStatus(OnboardingStatus.IN_PROGRESS);
        userRepository.save(user);

        boolean allSuccess = true;
        boolean anySuccess = false;

        for (ConnectorConfig config : enabledConfigs) {
            OnboardingTask task = createTask(user, config.getConnectorType());

            Optional<OnboardingConnector> connectorOpt = connectorRegistry.getConnector(config.getConnectorType());
            if (connectorOpt.isEmpty()) {
                task.setStatus(OnboardingStatus.FAILED);
                task.setErrorMessage("No connector implementation found for type: " + config.getConnectorType());
                task.setCompletedAt(LocalDateTime.now());
                taskRepository.save(task);
                allSuccess = false;
                continue;
            }

            task.setStatus(OnboardingStatus.IN_PROGRESS);
            task.setStartedAt(LocalDateTime.now());
            taskRepository.save(task);

            try {
                ConnectorExecutionResult result = connectorOpt.get().executeOnboarding(user, config);
                if (result.isSuccess()) {
                    task.setStatus(OnboardingStatus.COMPLETED);
                    task.setResultMessage(result.getMessage());
                    task.setExternalId(result.getExternalId());
                    anySuccess = true;
                } else {
                    task.setStatus(OnboardingStatus.FAILED);
                    task.setErrorMessage(result.getMessage());
                    allSuccess = false;
                }
            } catch (Exception e) {
                logger.error("Error executing connector {} for user {}", config.getConnectorType(), user.getEmail(), e);
                task.setStatus(OnboardingStatus.FAILED);
                task.setErrorMessage("Unexpected error: " + e.getMessage());
                allSuccess = false;
            }

            task.setCompletedAt(LocalDateTime.now());
            taskRepository.save(task);
        }

        if (allSuccess) {
            user.setStatus(OnboardingStatus.COMPLETED);
        } else if (anySuccess) {
            user.setStatus(OnboardingStatus.PARTIALLY_COMPLETED);
        } else {
            user.setStatus(OnboardingStatus.FAILED);
        }
        user.setCompletedAt(LocalDateTime.now());

        return userRepository.save(user);
    }

    /**
     * Get tasks for a specific user.
     */
    public List<OnboardingTask> getTasksForUser(Long userId) {
        return taskRepository.findByOnboardingUserId(userId);
    }

    /**
     * Re-execute a specific failed task.
     */
    @Transactional
    public OnboardingTask retryTask(Long taskId) {
        OnboardingTask task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found with id: " + taskId));

        if (task.getStatus() != OnboardingStatus.FAILED) {
            throw new RuntimeException("Can only retry failed tasks");
        }

        OnboardingUser user = task.getOnboardingUser();
        ConnectorType connectorType = task.getConnectorType();

        List<ConnectorConfig> configs = connectorConfigRepository.findByConnectorType(connectorType);
        ConnectorConfig config = configs.stream()
                .filter(ConnectorConfig::isEnabled)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No enabled config found for connector: " + connectorType));

        Optional<OnboardingConnector> connectorOpt = connectorRegistry.getConnector(connectorType);
        if (connectorOpt.isEmpty()) {
            throw new RuntimeException("No connector implementation found for type: " + connectorType);
        }

        task.setStatus(OnboardingStatus.IN_PROGRESS);
        task.setStartedAt(LocalDateTime.now());
        task.setErrorMessage(null);
        taskRepository.save(task);

        try {
            ConnectorExecutionResult result = connectorOpt.get().executeOnboarding(user, config);
            if (result.isSuccess()) {
                task.setStatus(OnboardingStatus.COMPLETED);
                task.setResultMessage(result.getMessage());
                task.setExternalId(result.getExternalId());
            } else {
                task.setStatus(OnboardingStatus.FAILED);
                task.setErrorMessage(result.getMessage());
            }
        } catch (Exception e) {
            task.setStatus(OnboardingStatus.FAILED);
            task.setErrorMessage("Unexpected error: " + e.getMessage());
        }

        task.setCompletedAt(LocalDateTime.now());
        taskRepository.save(task);

        // Recalculate user status
        updateUserStatus(user);

        return task;
    }

    private OnboardingTask createTask(OnboardingUser user, ConnectorType connectorType) {
        OnboardingTask task = new OnboardingTask();
        task.setOnboardingUser(user);
        task.setConnectorType(connectorType);
        task.setStatus(OnboardingStatus.PENDING);
        user.getTasks().add(task);
        return taskRepository.save(task);
    }

    private void updateUserStatus(OnboardingUser user) {
        List<OnboardingTask> tasks = taskRepository.findByOnboardingUserId(user.getId());
        boolean allCompleted = tasks.stream().allMatch(t -> t.getStatus() == OnboardingStatus.COMPLETED);
        boolean anyCompleted = tasks.stream().anyMatch(t -> t.getStatus() == OnboardingStatus.COMPLETED);
        boolean anyFailed = tasks.stream().anyMatch(t -> t.getStatus() == OnboardingStatus.FAILED);

        if (allCompleted) {
            user.setStatus(OnboardingStatus.COMPLETED);
        } else if (anyCompleted && anyFailed) {
            user.setStatus(OnboardingStatus.PARTIALLY_COMPLETED);
        } else if (anyFailed) {
            user.setStatus(OnboardingStatus.FAILED);
        }
        userRepository.save(user);
    }
}
