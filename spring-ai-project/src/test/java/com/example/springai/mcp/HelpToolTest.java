package com.example.springai.mcp;

import com.example.springai.entity.Project;
import com.example.springai.service.ProjectService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class HelpToolTest {

    @Mock
    private ProjectService projectService;

    @InjectMocks
    private HelpTool helpTool;

    private Map<String, String> parameters;

    @BeforeEach
    public void setup() {
        parameters = new HashMap<>();
    }

    @Test
    public void testExecute() {
        ToolResult result = helpTool.execute(parameters);
        
        assertTrue(result.isSuccess());
        assertNotNull(result.getMessage());
        assertTrue(result.getMessage().contains("Available commands"));
    }

    @Test
    public void testGetParameterNames() {
        String[] paramNames = helpTool.getParameterNames();
        
        assertEquals(0, paramNames.length);
    }
}
