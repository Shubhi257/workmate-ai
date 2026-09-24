package com.workmate.workmate_ai.controller;

import com.workmate.workmate_ai.dto.ChatApiResponse;
import com.workmate.workmate_ai.dto.ChatRequest;
import com.workmate.workmate_ai.dto.ConversationResponse;
import com.workmate.workmate_ai.service.ChatService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping
    public ChatApiResponse chat(@RequestBody ChatRequest request) {

        return chatService.chat(
                request.getConversationId(),
                request.getQuestion()
        );
    }

    @GetMapping("/conversations")
    public List<ConversationResponse> getConversations() {

        return chatService.getConversations();
    }
}