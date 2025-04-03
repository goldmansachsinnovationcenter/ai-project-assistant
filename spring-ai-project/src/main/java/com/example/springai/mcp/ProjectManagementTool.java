package com.example.springai.mcp;

import java.util.Map;

/**
 * Base class for all project management tools
 */
public abstract class ProjectManagementTool implements Tool {
    private final String name;
    private final String description;
    private static final String BASE_URI = "project-assistant";
    
    protected ProjectManagementTool(String name, String description) {
        this.name = name;
        this.description = description;
    }
    
    @Override
    public String getName() {
        return name;
    }
    
    @Override
    public String getDescription() {
        return description;
    }
    
    @Override
    public String getUri() {
        return BASE_URI + "/" + name;
    }
}
