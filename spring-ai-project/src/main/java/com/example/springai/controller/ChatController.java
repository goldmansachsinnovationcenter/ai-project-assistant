package com.example.springai.controller;

import com.example.springai.tools.ProjectManagementTools;
import com.siva.ai.tools.DateTimeTools;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class ChatController {

    private final OllamaChatModel chatModel;
    private final ProjectManagementTools projectManagementTools;

    public ChatController(OllamaChatModel chatModel, ProjectManagementTools projectManagementTools) {
        this.chatModel = chatModel;
        this.projectManagementTools = projectManagementTools;
    }

    @GetMapping("/ai/chat")
    public Map<String, String> generate(
            @RequestParam(value = "message", defaultValue = "Tell me a joke") String message) {

        String response = ChatClient.create(chatModel)
                .prompt(message)
                .tools(new DateTimeTools(), projectManagementTools)
                .call()
                .content();

        System.out.println(response);


        return Map.of("generation", response != null? response : "No Response");
    }
}
