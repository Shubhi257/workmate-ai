package com.workmate.workmate_ai.service;

import com.workmate.workmate_ai.dto.ChatResponse;
import org.junit.jupiter.api.Test;
import org.mockito.Answers;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class RagServiceTest {

    @Test
    void ask_shouldReturnAnswerFromAiUsingRetrievedDocuments() {

        // Arrange
        VectorStoreService vectorStoreService =
                mock(VectorStoreService.class);

        ChatClient.Builder chatClientBuilder =
                mock(ChatClient.Builder.class);

        ChatClient chatClient =
                mock(ChatClient.class, Answers.RETURNS_DEEP_STUBS);

        when(chatClientBuilder.build())
                .thenReturn(chatClient);

        RagService ragService =
                new RagService(chatClientBuilder, vectorStoreService);

        Document document = new Document(
                "Employees can work from home up to 2 days per week."
        );

        when(vectorStoreService.search(anyString()))
                .thenReturn(List.of(document));

        when(chatClient
                .prompt()
                .user(anyString())
                .call()
                .content())
                .thenReturn(
                        "Employees can work from home up to 2 days per week."
                );

        // Act
        ChatResponse response =
                ragService.ask(
                        "How many days can an employee work from home?"
                );

        // Assert
        assertNotNull(response);

        assertEquals(
                "Employees can work from home up to 2 days per week.",
                response.getAnswer()
        );

        assertNotNull(response.getSources());

        verify(vectorStoreService)
                .search(anyString());

        verify(chatClient
                .prompt()
                .user(anyString())
                .call())
                .content();
    }
}