package com.example.springai.mcp;

import org.springframework.ai.mcp.server.tool.McpTool;
import org.springframework.ai.mcp.server.tool.McpToolDefinition;
import org.springframework.ai.mcp.server.tool.McpToolParameter;
import org.springframework.ai.mcp.server.tool.McpToolResult;

import java.util.Map;

/**
 * Interface for all tools that can be called by the LLM using MCP
 */
public interface Tool extends McpTool {
    /**
     * Get the name of the tool
     * @return the tool name
     */
    String getName();
    
    /**
     * Get the description of the tool
     * @return the tool description
     */
    String getDescription();
    
    /**
     * Get the URI of the tool
     * @return the tool URI
     */
    String getUri();
    
    /**
     * Get the parameter names that this tool accepts
     * @return array of parameter names
     */
    String[] getParameterNames();
    
    /**
     * Execute the tool with the given parameters
     * @param parameters Map of parameter names to values
     * @return the result of the tool execution
     */
    ToolResult execute(Map<String, String> parameters);
    
    /**
     * Get the MCP tool definition
     * @return the MCP tool definition
     */
    @Override
    default McpToolDefinition getToolDefinition() {
        McpToolDefinition.Builder builder = McpToolDefinition.builder()
                .withName(getName())
                .withDescription(getDescription());
        
        for (String paramName : getParameterNames()) {
            builder.withParameter(McpToolParameter.builder()
                    .withName(paramName)
                    .withDescription("Parameter: " + paramName)
                    .withRequired(paramName.equals("name") || paramName.equals("project"))
                    .withType("string")
                    .build());
        }
        
        return builder.build();
    }
    
    /**
     * Execute the tool with the given parameters using MCP
     * @param parameters Map of parameter names to values
     * @return the MCP tool result
     */
    @Override
    default McpToolResult execute(Map<String, Object> parameters) {
        Map<String, String> stringParams = parameters.entrySet().stream()
                .collect(java.util.stream.Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue() != null ? entry.getValue().toString() : ""
                ));
        
        ToolResult result = execute(stringParams);
        
        return result.isSuccess() 
                ? McpToolResult.success(result.getMessage()) 
                : McpToolResult.failure(result.getMessage());
    }
}
