package com.workmate.workmate_ai.service;

import com.workmate.workmate_ai.entity.ChatMessage;
import com.workmate.workmate_ai.entity.Conversation;
import com.workmate.workmate_ai.entity.User;
import com.workmate.workmate_ai.repository.ChatMessageRepository;
import com.workmate.workmate_ai.repository.ConversationRepository;
import com.workmate.workmate_ai.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ChatServiceTest {

    @Test
    void chat_shouldCreateConversationAndSaveMessages() {

        // Arrange
        ConversationRepository conversationRepository =
                mock(ConversationRepository.class);

        ChatMessageRepository chatMessageRepository =
                mock(ChatMessageRepository.class);

        UserRepository userRepository =
                mock(UserRepository.class);

        RagService ragService =
                mock(RagService.class);

        ChatService chatService =
                new ChatService(
                        conversationRepository,
                        chatMessageRepository,
                        userRepository,
                        ragService
                );

        User user = new User();
        user.setId(1L);
        user.setName("Test User");
        user.setEmail("test@example.com");

        when(userRepository.findByEmail("test@example.com"))
                .thenReturn(Optional.of(user));

        Conversation conversation = new Conversation();
        conversation.setId(1L);
        conversation.setUser(user);

        when(conversationRepository.save(any(Conversation.class)))
                .thenReturn(conversation);

        when(chatMessageRepository
                .findByConversationIdOrderByCreatedAtAsc(1L))
                .thenReturn(List.of());

        when(ragService.ask(
                eq("How many days can I work from home?"),
                anyString()
        )).thenReturn(
                new com.workmate.workmate_ai.dto.ChatResponse(
                        "You can work from home up to 2 days per week.",
                        List.of()
                )
        );

        // Mock Spring Security context
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName())
                .thenReturn("test@example.com");

        SecurityContext securityContext =
                mock(SecurityContext.class);

        when(securityContext.getAuthentication())
                .thenReturn(authentication);

        // Act
        try (MockedStatic<SecurityContextHolder> mockedSecurityContext =
                     mockStatic(SecurityContextHolder.class)) {

            mockedSecurityContext
                    .when(SecurityContextHolder::getContext)
                    .thenReturn(securityContext);

            String answer = chatService.chat(
                    null,
                    "How many days can I work from home?"
            );

            // Assert
            assertEquals(
                    "You can work from home up to 2 days per week.",
                    answer
            );
        }

        verify(conversationRepository)
                .save(any(Conversation.class));

        verify(chatMessageRepository, times(2))
                .save(any(ChatMessage.class));

        verify(ragService)
                .ask(
                        eq("How many days can I work from home?"),
                        anyString()
                );
    }
}