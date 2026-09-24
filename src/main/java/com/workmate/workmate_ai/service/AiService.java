package com.workmate.workmate_ai.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class AiService {

    private final ChatClient chatClient;

    public AiService(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    @Cacheable("aiAnswers")
    public String ask(String question) {
        System.out.println("Calling AI model for: " + question);

        return chatClient
                .prompt()
                .user(question)
                .call()
                .content();
    }
}