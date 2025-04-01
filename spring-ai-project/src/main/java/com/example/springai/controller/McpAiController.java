package com.example.springai.controller;

import com.example.springai.entity.ChatMessage;
import com.example.springai.mcp.Tool;
import com.example.springai.mcp.ToolResult;
import com.example.springai.model.McpPromptTemplate;
import com.example.springai.repository.ChatMessageRepository;
import com.example.springai.service.McpToolService;
import org.springframework.ai.chat.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.ollama.OllamaChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Controller for handling AI chat with MCP tool calling
 */
@RestController
@RequestMapping("/api/ai")
public class McpAiController {
    
    @Autowired
    private OllamaChatClient chatClient;
    
    @Autowired
    private McpToolService mcpToolService;
    
    @Autowired
    private ChatMessageRepository chatMessageRepository;
    
    @Autowired
    private McpPromptTemplate promptTemplate;
    
    private static final Pattern TOOL_CALL_PATTERN = Pattern.compile("(?i)(?:use|call|execute)\\s+(?:the\\s+)?(?:tool\\s+)?['\"]?([\\w-]+)['\"]?|\\{\\s*\"tool\"\\s*:\\s*\"([^\"]+)\"");
    private static final Pattern PARAMETER_PATTERN = Pattern.compile("(?:\"([^\"]+)\"\\s*:\\s*\"([^\"]+)\"|([\\w-]+)\\s*=\\s*['\"]([^'\"]+)['\"]|([\\w-]+)\\s+is\\s+['\"]([^'\"]+)['\"]|([\\w-]+)\\s+['\"]([^'\"]+)['\"])");
    
    /**
     * Process a chat message using MCP tool calling
     * @param message User's message
     * @return Response from the AI or tool execution
     */
    @GetMapping("/mcp-chat")
    public String mcpChat(@RequestParam String message) {
        String response;
        
        try {
            if (message.trim().equalsIgnoreCase("help")) {
                Tool helpTool = mcpToolService.getToolByName("help");
                if (helpTool != null) {
                    return helpTool.execute(Map.of()).getMessage();
                }
            } else if (message.trim().equalsIgnoreCase("list projects")) {
                Tool listTool = mcpToolService.getToolByName("list-projects");
                if (listTool != null) {
                    return listTool.execute(Map.of()).getMessage();
                }
            } else if (message.toLowerCase().startsWith("create project ")) {
                String projectName = message.substring("create project ".length()).trim();
                Tool createTool = mcpToolService.getToolByName("create-project");
                if (createTool != null && !projectName.isEmpty()) {
                    return createTool.execute(Map.of("name", projectName)).getMessage();
                }
            } else if (message.toLowerCase().startsWith("show project ")) {
                String projectName = message.substring("show project ".length()).trim();
                Tool showTool = mcpToolService.getToolByName("show-project");
                if (showTool != null && !projectName.isEmpty()) {
                    return showTool.execute(Map.of("name", projectName)).getMessage();
                }
            }
            
            List<Tool> tools = mcpToolService.getAllTools();
            String promptText = promptTemplate.createToolCallingPrompt(message, tools);
            
            ChatResponse aiResponse = chatClient.call(new Prompt(promptText));
            String llmResponse = aiResponse.getResult().getOutput().getContent();
            
            System.out.println("DEBUG - LLM Response: " + llmResponse);
            
            if (containsToolCall(llmResponse)) {
                System.out.println("DEBUG - Tool call detected");
                Map.Entry<String, Map<String, String>> toolCall = extractToolCall(llmResponse);
                if (toolCall != null) {
                    String toolName = toolCall.getKey();
                    Map<String, String> parameters = toolCall.getValue();
                    
                    System.out.println("DEBUG - Extracted tool: " + toolName);
                    System.out.println("DEBUG - Parameters: " + parameters);
                    
                    Tool tool = mcpToolService.getToolByName(toolName);
                    if (tool != null) {
                        ToolResult toolResult = tool.execute(parameters);
                        response = toolResult.getMessage();
                    } else {
                        response = "I couldn't find a tool named '" + toolName + "'. Type 'help' to see available commands.";
                    }
                } else {
                    System.out.println("DEBUG - Failed to extract tool call");
                    if (message.toLowerCase().contains("add requirement") && message.toLowerCase().contains("project")) {
                        response = "To add a requirement, please use the format: 'add requirement [text] to project [name]'";
                    } else if (message.toLowerCase().contains("prepare stories") && message.toLowerCase().contains("project")) {
                        response = "To prepare stories, please use the format: 'prepare stories for project [name]'";
                    } else {
                        response = "I'm not sure what you're asking for. Type 'help' to see available commands.";
                    }
                }
            } else {
                System.out.println("DEBUG - No tool call detected");
                response = "I'm not sure how to help with that. Type 'help' to see available commands.";
            }
        } catch (Exception e) {
            response = "I'm sorry, I encountered an error processing your request. Please try again or use one of the available commands. Type 'help' to see the available commands.";
            System.err.println("Error in MCP AI chat: " + e.getMessage());
            e.printStackTrace();
        }
        
        saveChatMessage(message, response);
        
        return response;
    }
    
    /**
     * Save a chat message to the repository
     * @param prompt User's message
     * @param response AI's response
     */
    private void saveChatMessage(String prompt, String response) {
        ChatMessage chatMessage = new ChatMessage(prompt, response);
        chatMessageRepository.save(chatMessage);
    }
    
    /**
     * Check if the LLM response contains a tool call
     * @param response LLM response text
     * @return true if the response contains a tool call
     */
    private boolean containsToolCall(String response) {
        if (TOOL_CALL_PATTERN.matcher(response).find()) {
            return true;
        }
        
        if (response.toLowerCase().contains("create project") ||
            response.toLowerCase().contains("list projects") ||
            response.toLowerCase().contains("show project") ||
            response.toLowerCase().contains("add requirement") ||
            response.toLowerCase().contains("prepare stories") ||
            response.toLowerCase().equals("help")) {
            return true;
        }
        
        Pattern naturalLanguagePattern = Pattern.compile("(?i)(?:create|list|show|add|prepare|help)\\s+(?:project|projects|requirement|stories)");
        return naturalLanguagePattern.matcher(response).find();
    }
    
    /**
     * Extract tool name and parameters from LLM response
     * @param response LLM response text
     * @return Map.Entry with tool name as key and parameters map as value, or null if extraction fails
     */
    private Map.Entry<String, Map<String, String>> extractToolCall(String response) {
        Matcher matcher = TOOL_CALL_PATTERN.matcher(response);
        if (matcher.find()) {
            String toolName = matcher.group(1) != null ? matcher.group(1) : matcher.group(2);
            
            if (toolName == null) {
                Pattern toolNamePattern = Pattern.compile("(?i)(?:use|call|execute|create|list|show|add|prepare|help)\\s+(?:the\\s+)?(?:tool\\s+)?['\"]?([\\w-]+)['\"]?");
                Matcher toolNameMatcher = toolNamePattern.matcher(response);
                if (toolNameMatcher.find()) {
                    toolName = toolNameMatcher.group(1);
                }
            }
            
            if (toolName == null) {
                return null;
            }
            
            if (toolName.equalsIgnoreCase("create")) toolName = "create-project";
            if (toolName.equalsIgnoreCase("list")) toolName = "list-projects";
            if (toolName.equalsIgnoreCase("show")) toolName = "show-project";
            if (toolName.equalsIgnoreCase("add")) toolName = "add-requirement";
            if (toolName.equalsIgnoreCase("prepare")) toolName = "prepare-stories";
            
            Map<String, String> parameters = new HashMap<>();
            
            Matcher paramMatcher = PARAMETER_PATTERN.matcher(response);
            while (paramMatcher.find()) {
                String paramName = null;
                String paramValue = null;
                
                if (paramMatcher.group(1) != null && paramMatcher.group(2) != null) {
                    paramName = paramMatcher.group(1);
                    paramValue = paramMatcher.group(2);
                } else if (paramMatcher.group(3) != null && paramMatcher.group(4) != null) {
                    paramName = paramMatcher.group(3);
                    paramValue = paramMatcher.group(4);
                } else if (paramMatcher.group(5) != null && paramMatcher.group(6) != null) {
                    paramName = paramMatcher.group(5);
                    paramValue = paramMatcher.group(6);
                } else if (paramMatcher.group(7) != null && paramMatcher.group(8) != null) {
                    paramName = paramMatcher.group(7);
                    paramValue = paramMatcher.group(8);
                }
                
                if (paramName != null && paramValue != null) {
                    parameters.put(paramName, paramValue);
                }
            }
            
            if (!parameters.containsKey("name") && !parameters.containsKey("project")) {
                Pattern projectNamePattern = Pattern.compile("(?i)(?:project|for project)\\s+['\"]?([\\w-]+)['\"]?");
                Matcher projectNameMatcher = projectNamePattern.matcher(response);
                if (projectNameMatcher.find()) {
                    String projectName = projectNameMatcher.group(1);
                    if (toolName.equals("create-project")) {
                        parameters.put("name", projectName);
                    } else {
                        parameters.put("project", projectName);
                    }
                }
            }
            
            if (toolName.equals("add-requirement") && !parameters.containsKey("requirement")) {
                Pattern reqPattern = Pattern.compile("(?i)requirement\\s+['\"]?([^'\"]+)['\"]?\\s+(?:to|for)");
                Matcher reqMatcher = reqPattern.matcher(response);
                if (reqMatcher.find()) {
                    parameters.put("requirement", reqMatcher.group(1));
                }
            }
            
            System.out.println("DEBUG - Extracted tool: " + toolName);
            System.out.println("DEBUG - Extracted parameters: " + parameters);
            
            return Map.entry(toolName, parameters);
        }
        return null;
    }
}
