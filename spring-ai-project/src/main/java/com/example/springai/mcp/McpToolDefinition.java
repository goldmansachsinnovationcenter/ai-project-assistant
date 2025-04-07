package com.example.springai.mcp;

import java.util.ArrayList;
import java.util.List;

/**
 * Definition of an MCP tool
 */
public class McpToolDefinition {
    private String name;
    private String description;
    private List<McpToolParameter> parameters;
    
    private McpToolDefinition(Builder builder) {
        this.name = builder.name;
        this.description = builder.description;
        this.parameters = builder.parameters;
    }
    
    public String getName() {
        return name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public List<McpToolParameter> getParameters() {
        return parameters;
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    public static class Builder {
        private String name;
        private String description;
        private List<McpToolParameter> parameters = new ArrayList<>();
        
        public Builder withName(String name) {
            this.name = name;
            return this;
        }
        
        public Builder withDescription(String description) {
            this.description = description;
            return this;
        }
        
        public Builder withParameter(McpToolParameter parameter) {
            this.parameters.add(parameter);
            return this;
        }
        
        public McpToolDefinition build() {
            return new McpToolDefinition(this);
        }
    }
}
