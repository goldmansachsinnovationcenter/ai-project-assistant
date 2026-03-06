package com.example.springai.onboarding.controller;

import com.example.springai.onboarding.connector.ConnectorExecutionResult;
import com.example.springai.onboarding.connector.ConnectorType;
import com.example.springai.onboarding.connector.ConnectorValidationResult;
import com.example.springai.onboarding.entity.ConnectorConfig;
import com.example.springai.onboarding.service.ConnectorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * REST controller for managing connector configurations in GSIC-TRACK.
 */
@RestController
@RequestMapping("/api/onboarding/connectors")
public class ConnectorController {

    private final ConnectorService connectorService;

    public ConnectorController(ConnectorService connectorService) {
        this.connectorService = connectorService;
    }

    /**
     * Get all available connector types with metadata.
     */
    @GetMapping("/types")
    public ResponseEntity<List<Map<String, String>>> getConnectorTypes() {
        List<Map<String, String>> types = new ArrayList<>();
        for (ConnectorType type : connectorService.getAvailableConnectorTypes()) {
            Map<String, String> typeInfo = new LinkedHashMap<>();
            typeInfo.put("type", type.name());
            typeInfo.put("displayName", type.getDisplayName());
            typeInfo.put("description", type.getDescription());
            types.add(typeInfo);
        }
        return ResponseEntity.ok(types);
    }

    /**
     * Get all connector configurations.
     */
    @GetMapping
    public ResponseEntity<List<ConnectorConfig>> getAllConfigs() {
        return ResponseEntity.ok(connectorService.getAllConfigs());
    }

    /**
     * Get a connector configuration by ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ConnectorConfig> getConfigById(@PathVariable Long id) {
        return connectorService.getConfigById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Get all enabled connector configurations.
     */
    @GetMapping("/enabled")
    public ResponseEntity<List<ConnectorConfig>> getEnabledConfigs() {
        return ResponseEntity.ok(connectorService.getEnabledConfigs());
    }

    /**
     * Create a new connector configuration.
     */
    @PostMapping
    public ResponseEntity<?> createConfig(@RequestBody ConnectorConfig config) {
        ConnectorValidationResult validation = connectorService.validateConfig(config);
        if (!validation.isValid()) {
            Map<String, Object> errorResponse = new LinkedHashMap<>();
            errorResponse.put("valid", false);
            errorResponse.put("errors", validation.getErrors());
            return ResponseEntity.badRequest().body(errorResponse);
        }
        ConnectorConfig created = connectorService.createConfig(config);
        return ResponseEntity.ok(created);
    }

    /**
     * Update an existing connector configuration.
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateConfig(@PathVariable Long id, @RequestBody ConnectorConfig config) {
        ConnectorValidationResult validation = connectorService.validateConfig(config);
        if (!validation.isValid()) {
            Map<String, Object> errorResponse = new LinkedHashMap<>();
            errorResponse.put("valid", false);
            errorResponse.put("errors", validation.getErrors());
            return ResponseEntity.badRequest().body(errorResponse);
        }
        return connectorService.updateConfig(id, config)
                .map(updated -> ResponseEntity.ok((Object) updated))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Delete a connector configuration.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteConfig(@PathVariable Long id) {
        if (connectorService.deleteConfig(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * Toggle the enabled status of a connector.
     */
    @PutMapping("/{id}/toggle")
    public ResponseEntity<ConnectorConfig> toggleEnabled(@PathVariable Long id) {
        return connectorService.toggleEnabled(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Test the connection for a connector configuration.
     */
    @PostMapping("/{id}/test")
    public ResponseEntity<ConnectorExecutionResult> testConnection(@PathVariable Long id) {
        ConnectorExecutionResult result = connectorService.testConnection(id);
        if (result.isSuccess()) {
            return ResponseEntity.ok(result);
        }
        return ResponseEntity.ok(result);
    }

    /**
     * Validate a connector configuration without saving.
     */
    @PostMapping("/validate")
    public ResponseEntity<ConnectorValidationResult> validateConfig(@RequestBody ConnectorConfig config) {
        ConnectorValidationResult result = connectorService.validateConfig(config);
        return ResponseEntity.ok(result);
    }
}
