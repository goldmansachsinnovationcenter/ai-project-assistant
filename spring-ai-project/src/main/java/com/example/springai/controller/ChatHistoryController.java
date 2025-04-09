package com.example.springai.controller;

import com.example.springai.entity.ChatMessage;
import com.example.springai.repository.ChatMessageRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ChatHistoryController {

    private final ChatMessageRepository chatMessageRepository;

    public ChatHistoryController(ChatMessageRepository chatMessageRepository) {
        this.chatMessageRepository = chatMessageRepository;
    }

    @GetMapping({"/ai/chat-history", "/api/ai/chat-history"})
    public List<ChatMessage> getChatHistory(
            @RequestParam(value = "limit", defaultValue = "20") int limit) {
        return chatMessageRepository.findRecentMessages(limit);
    }
}
