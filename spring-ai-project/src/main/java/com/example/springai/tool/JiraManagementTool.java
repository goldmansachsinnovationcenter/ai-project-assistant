package com.example.springai.tool;

/**
 * Base class for all Jira management tools
 */
public abstract class JiraManagementTool implements Tool {
    private final String name;
    private final String description;
    private static final String BASE_URI = "jira-assistant";
    
    public JiraManagementTool(String name, String description) {
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
