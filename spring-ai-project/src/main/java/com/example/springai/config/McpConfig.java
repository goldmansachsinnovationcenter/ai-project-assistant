package com.example.springai.config;

import com.example.springai.mcp.*;
import org.springframework.ai.mcp.server.McpServer;
import org.springframework.ai.mcp.server.McpServerBuilder;
import org.springframework.ai.mcp.server.transport.webmvc.WebMvcMcpServerTransport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
     * Configure the MCP server with WebMVC transport
     * @param transport WebMVC transport for the MCP server
     * @return Configured MCP server
     */
    @Bean
    public McpServer mcpServer(WebMvcMcpServerTransport transport) {
        return McpServerBuilder.builder()
                .withTransport(transport)
                .withTool(createProjectTool)
                .withTool(listProjectsTool)
                .withTool(showProjectTool)
                .withTool(addRequirementTool)
                .withTool(prepareStoriesTool)
                .withTool(helpTool)
                .build();
    }
}
