package com.workmate.workmate_ai.controller;

import com.workmate.workmate_ai.dto.ChatRequest;
import com.workmate.workmate_ai.service.ChatService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping
    public String chat(@RequestBody ChatRequest request) {

        return chatService.chat(
                request.getConversationId(),
                request.getQuestion()
        );
    }
}