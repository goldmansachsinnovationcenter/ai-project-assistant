package com.example.springai.config;

import com.example.springai.mcp.*;
import com.example.springai.service.McpToolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.List;

/**
 * Configuration for MCP server and tools
 */
@Configuration
public class McpConfig {

    @Autowired
    private CreateProjectTool createProjectTool;

    @Autowired
    private ListProjectsTool listProjectsTool;

    @Autowired
    private ShowProjectTool showProjectTool;

    @Autowired
    private AddRequirementTool addRequirementTool;

    @Autowired
    private PrepareStoriesTool prepareStoriesTool;

    @Autowired
    private HelpTool helpTool;

    /**
     * Configure the MCP client with available tools
     * @return Configured MCP client
     */
    @Bean
    public McpClient mcpClient() {
        List<Tool> tools = Arrays.asList(
                createProjectTool,
                listProjectsTool,
                showProjectTool,
                addRequirementTool,
                prepareStoriesTool,
                helpTool
        );
        return new McpClient(tools);
    }
    
    /**
     * Configure the MCP tool service with available tools
     * @return Configured MCP tool service
     */
    @Bean
    public McpToolService mcpToolService() {
        return new McpToolService(
                createProjectTool,
                listProjectsTool,
                showProjectTool,
                addRequirementTool,
                prepareStoriesTool,
                helpTool
        );
    }
}
