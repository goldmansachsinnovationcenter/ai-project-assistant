package com.example.springai.onboarding.controller;

import com.example.springai.onboarding.entity.OnboardingStatus;
import com.example.springai.onboarding.entity.OnboardingTask;
import com.example.springai.onboarding.entity.OnboardingUser;
import com.example.springai.onboarding.service.OnboardingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * REST controller for managing user onboarding in GSIC-TRACK.
 */
@RestController
@RequestMapping("/api/onboarding/users")
public class OnboardingController {

    private final OnboardingService onboardingService;

    public OnboardingController(OnboardingService onboardingService) {
        this.onboardingService = onboardingService;
    }

    /**
     * Get all onboarding users.
     */
    @GetMapping
    public ResponseEntity<List<OnboardingUser>> getAllUsers() {
        return ResponseEntity.ok(onboardingService.getAllUsers());
    }

    /**
     * Get an onboarding user by ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<OnboardingUser> getUserById(@PathVariable Long id) {
        return onboardingService.getUserById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Get onboarding users by status.
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<List<OnboardingUser>> getUsersByStatus(@PathVariable OnboardingStatus status) {
        return ResponseEntity.ok(onboardingService.getUsersByStatus(status));
    }

    /**
     * Create a new onboarding user.
     */
    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody OnboardingUser user) {
        if (user.getFirstName() == null || user.getFirstName().isBlank()) {
            return ResponseEntity.badRequest().body(errorResponse("First name is required"));
        }
        if (user.getLastName() == null || user.getLastName().isBlank()) {
            return ResponseEntity.badRequest().body(errorResponse("Last name is required"));
        }
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            return ResponseEntity.badRequest().body(errorResponse("Email is required"));
        }

        // Check for duplicate email
        if (onboardingService.getUserByEmail(user.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body(errorResponse("User with email '" + user.getEmail() + "' already exists"));
        }

        OnboardingUser created = onboardingService.createUser(user);
        return ResponseEntity.ok(created);
    }

    /**
     * Update an existing onboarding user.
     */
    @PutMapping("/{id}")
    public ResponseEntity<OnboardingUser> updateUser(@PathVariable Long id, @RequestBody OnboardingUser user) {
        return onboardingService.updateUser(id, user)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Delete an onboarding user.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        if (onboardingService.deleteUser(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * Execute onboarding for a user across all enabled connectors.
     */
    @PostMapping("/{id}/execute")
    public ResponseEntity<?> executeOnboarding(@PathVariable Long id) {
        try {
            OnboardingUser user = onboardingService.executeOnboarding(id);
            return ResponseEntity.ok(user);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(errorResponse(e.getMessage()));
        }
    }

    /**
     * Get onboarding tasks for a specific user.
     */
    @GetMapping("/{id}/tasks")
    public ResponseEntity<List<OnboardingTask>> getTasksForUser(@PathVariable Long id) {
        return ResponseEntity.ok(onboardingService.getTasksForUser(id));
    }

    /**
     * Retry a failed onboarding task.
     */
    @PostMapping("/tasks/{taskId}/retry")
    public ResponseEntity<?> retryTask(@PathVariable Long taskId) {
        try {
            OnboardingTask task = onboardingService.retryTask(taskId);
            return ResponseEntity.ok(task);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(errorResponse(e.getMessage()));
        }
    }

    /**
     * Get onboarding dashboard stats.
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        Map<String, Object> stats = new LinkedHashMap<>();
        List<OnboardingUser> allUsers = onboardingService.getAllUsers();
        stats.put("totalUsers", allUsers.size());
        stats.put("pending", allUsers.stream().filter(u -> u.getStatus() == OnboardingStatus.PENDING).count());
        stats.put("inProgress", allUsers.stream().filter(u -> u.getStatus() == OnboardingStatus.IN_PROGRESS).count());
        stats.put("completed", allUsers.stream().filter(u -> u.getStatus() == OnboardingStatus.COMPLETED).count());
        stats.put("failed", allUsers.stream().filter(u -> u.getStatus() == OnboardingStatus.FAILED).count());
        stats.put("partiallyCompleted", allUsers.stream().filter(u -> u.getStatus() == OnboardingStatus.PARTIALLY_COMPLETED).count());
        return ResponseEntity.ok(stats);
    }

    private Map<String, String> errorResponse(String message) {
        Map<String, String> error = new LinkedHashMap<>();
        error.put("error", message);
        return error;
    }
}
