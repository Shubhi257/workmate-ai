package com.workmate.workmate_ai.dto;

import java.time.LocalDateTime;

public class ConversationResponse {

    private Long id;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public ConversationResponse() {
    }

    public ConversationResponse(
            Long id,
            LocalDateTime createdAt,
            LocalDateTime updatedAt) {

        this.id = id;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() {
        return id;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}