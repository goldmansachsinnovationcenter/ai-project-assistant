package com.example.springai.service;

import com.example.springai.mcp.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service for managing MCP tools
 */
@Service
public class McpToolService {
    private final List<Tool> tools;

    @Autowired
    public McpToolService(
            CreateProjectTool createProjectTool,
            ListProjectsTool listProjectsTool,
            ShowProjectTool showProjectTool,
            AddRequirementTool addRequirementTool,
            PrepareStoriesTool prepareStoriesTool,
            HelpTool helpTool) {
        this.tools = List.of(
            createProjectTool,
            listProjectsTool,
            showProjectTool,
            addRequirementTool,
            prepareStoriesTool,
            helpTool
        );
    }

    /**
     * Get all registered tools
     * @return List of all tools
     */
    public List<Tool> getAllTools() {
        return tools;
    }
    
    /**
     * Find a tool by name
     * @param name Tool name
     * @return Tool if found, null otherwise
     */
    public Tool getToolByName(String name) {
        return tools.stream()
                .filter(tool -> tool.getName().equals(name))
                .findFirst()
                .orElse(null);
    }
    
    /**
     * Execute a tool by name with parameters
     * @param toolName Name of the tool to execute
     * @param parameters Parameters for the tool
     * @return Result of the tool execution
     */
    public ToolResult executeTool(String toolName, Map<String, String> parameters) {
        Tool tool = getToolByName(toolName);
        if (tool == null) {
            return ToolResult.failure(String.format("Tool '%s' not found", toolName));
        }
        
        return tool.execute(parameters);
    }
    
    /**
     * Get all tools for use with the MCP client
     * @return List of tools
     */
    public List<Tool> getMcpTools() {
        return tools;
    }
    
    /**
     * Get all tools (alias for getAllTools)
     * @return List of all tools
     */
    public List<Tool> getTools() {
        return getAllTools();
    }
}
