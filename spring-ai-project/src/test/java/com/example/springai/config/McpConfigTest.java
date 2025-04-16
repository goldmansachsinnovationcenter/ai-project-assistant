package com.example.springai.config;

import com.example.springai.tool.*; // Corrected import
import com.example.springai.mcp.McpClient; // Keep needed mcp import
import com.example.springai.service.McpToolService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationContext;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class McpConfigTest {

    @Mock private CreateProjectTool createProjectTool;
    @Mock private ListProjectsTool listProjectsTool;
    @Mock private ShowProjectTool showProjectTool;
    @Mock private AddRequirementTool addRequirementTool;
    @Mock private HelpTool helpTool;

    @InjectMocks // Inject mocks into McpConfig
    private McpConfig mcpConfig;

    @BeforeEach
    void setUp() {
        lenient().when(createProjectTool.getName()).thenReturn("createProject");
        lenient().when(listProjectsTool.getName()).thenReturn("listProjects");
        lenient().when(showProjectTool.getName()).thenReturn("showProject");
        lenient().when(addRequirementTool.getName()).thenReturn("addRequirement");
        lenient().when(helpTool.getName()).thenReturn("help");
    }

    @Test
    void mcpClient_BeanCreation_InjectsCorrectTools() {
        McpClient mcpClient = mcpConfig.mcpClient();

        assertNotNull(mcpClient, "McpClient bean should not be null");

        List<Tool> injectedTools = mcpClient.getTools();
        assertNotNull(injectedTools, "Injected tools list should not be null");
        assertEquals(5, injectedTools.size(), "Should contain 5 tools (PrepareStoriesTool removed)");

        assertTrue(injectedTools.stream().anyMatch(t -> t == createProjectTool), "CreateProjectTool missing");
        assertTrue(injectedTools.stream().anyMatch(t -> t == listProjectsTool), "ListProjectsTool missing");
        assertTrue(injectedTools.stream().anyMatch(t -> t == showProjectTool), "ShowProjectTool missing");
        assertTrue(injectedTools.stream().anyMatch(t -> t == addRequirementTool), "AddRequirementTool missing");
        assertTrue(injectedTools.stream().anyMatch(t -> t == helpTool), "HelpTool missing");

        assertFalse(injectedTools.stream().anyMatch(t -> "prepareStories".equals(t.getName())), "PrepareStoriesTool should not be present");
    }

    @Test
    void mcpToolService_BeanCreation_InjectsCorrectTools() {
        McpToolService mcpToolService = mcpConfig.mcpToolService();

        assertNotNull(mcpToolService, "McpToolService bean should not be null");

        List<Tool> serviceTools = mcpToolService.getAllTools(); // Assuming getAllTools returns the list
        assertNotNull(serviceTools, "Service tools list should not be null");
        assertEquals(5, serviceTools.size(), "Should contain 5 tools (PrepareStoriesTool removed)");

        assertTrue(serviceTools.contains(createProjectTool), "CreateProjectTool missing in service");
        assertTrue(serviceTools.contains(listProjectsTool), "ListProjectsTool missing in service");
        assertTrue(serviceTools.contains(showProjectTool), "ShowProjectTool missing in service");
        assertTrue(serviceTools.contains(addRequirementTool), "AddRequirementTool missing in service");
        assertTrue(serviceTools.contains(helpTool), "HelpTool missing in service");

         assertFalse(serviceTools.stream().anyMatch(t -> "prepareStories".equals(t.getName())), "PrepareStoriesTool should not be present in service");

    }
}
