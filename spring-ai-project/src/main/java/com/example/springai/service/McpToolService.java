package com.example.springai.service;

import org.springframework.ai.mcp.server.McpServer;
import org.springframework.ai.mcp.server.McpTool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

/**
 * Service for managing MCP tools
 */
@Service
public class McpToolService {
    
    @Autowired
    private McpServer mcpServer;

    /**
     * Get all registered tools
     * @return List of all tools
     */
    public List<McpTool> getAllTools() {
        return mcpServer.getTools();
    }
    
    /**
     * Find a tool by name
     * @param name Tool name
     * @return Tool if found, null otherwise
     */
    public McpTool getToolByName(String name) {
        return mcpServer.getTools().stream()
                .filter(tool -> tool.getName().equals(name))
                .findFirst()
                .orElse(null);
    }
    
    /**
     * Execute a tool by name with parameters
     * @param toolName Name of the tool to execute
     * @param parameters Parameters for the tool
     * @return Result of the tool execution as a String
     */
    public String executeTool(String toolName, Map<String, Object> parameters) {
        McpTool tool = getToolByName(toolName);
        if (tool == null) {
            return String.format("Tool '%s' not found", toolName);
        }
        
        try {
            Optional<Function<Map<String, Object>, String>> executionFunction = 
                mcpServer.getToolExecutionFunction(tool.getName());
            
            if (executionFunction.isPresent()) {
                return executionFunction.get().apply(parameters);
            } else {
                return String.format("No execution function found for tool '%s'", toolName);
            }
        } catch (Exception e) {
            return "Error executing tool: " + e.getMessage();
        }
    }
    
    /**
     * Get all tools (alias for getAllTools)
     * @return List of all tools
     */
    public List<McpTool> getTools() {
        return getAllTools();
    }
}
