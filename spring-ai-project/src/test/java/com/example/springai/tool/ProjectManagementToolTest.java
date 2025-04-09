package com.example.springai.tool;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class ProjectManagementToolTest {

    private static class TestProjectManagementTool extends ProjectManagementTool {
        public TestProjectManagementTool() {
            super("test-tool", "Test tool description");
        }
        
        @Override
        public ToolResult execute(java.util.Map<String, String> parameters) {
            return ToolResult.success("Test execution");
        }
        
        @Override
        public String[] getParameterNames() {
            return new String[0];
        }
    }
    
    private final ProjectManagementTool projectManagementTool = new TestProjectManagementTool();

    @Test
    public void testGetName() {
        assertEquals("test-tool", projectManagementTool.getName());
    }

    @Test
    public void testGetDescription() {
        String description = projectManagementTool.getDescription();
        assertNotNull(description);
        assertEquals("Test tool description", description);
    }

    @Test
    public void testGetUri() {
        String uri = projectManagementTool.getUri();
        assertNotNull(uri);
        assertTrue(uri.contains("project-assistant/test-tool"));
    }
}
