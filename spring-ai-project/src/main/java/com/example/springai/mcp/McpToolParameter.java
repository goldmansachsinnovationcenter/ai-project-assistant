package com.example.springai.mcp;

/**
 * Parameter for an MCP tool
 */
public class McpToolParameter {
    private String name;
    private String description;
    private boolean required;
    private String type;
    
    private McpToolParameter(Builder builder) {
        this.name = builder.name;
        this.description = builder.description;
        this.required = builder.required;
        this.type = builder.type;
    }
    
    public String getName() {
        return name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public boolean isRequired() {
        return required;
    }
    
    public String getType() {
        return type;
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    public static class Builder {
        private String name;
        private String description;
        private boolean required;
        private String type = "string";
        
        public Builder withName(String name) {
            this.name = name;
            return this;
        }
        
        public Builder withDescription(String description) {
            this.description = description;
            return this;
        }
        
        public Builder withRequired(boolean required) {
            this.required = required;
            return this;
        }
        
        public Builder withType(String type) {
            this.type = type;
            return this;
        }
        
        public McpToolParameter build() {
            return new McpToolParameter(this);
        }
    }
}
