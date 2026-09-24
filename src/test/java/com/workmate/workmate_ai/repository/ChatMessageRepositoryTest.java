package com.workmate.workmate_ai.repository;

import com.workmate.workmate_ai.entity.ChatMessage;
import com.workmate.workmate_ai.entity.Conversation;
import com.workmate.workmate_ai.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Import(ChatMessageRepositoryTest.TestCacheConfig.class)
class ChatMessageRepositoryTest {

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @Autowired
    private ConversationRepository conversationRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void findByConversationIdOrderByCreatedAtAsc_shouldReturnMessagesInOrder() {

        User user = new User();
        user.setName("Test User");
        user.setEmail("chat@test.com");
        user.setPassword("hashedPassword");

        User savedUser = userRepository.save(user);

        Conversation conversation = new Conversation();
        conversation.setUser(savedUser);

        Conversation savedConversation =
                conversationRepository.save(conversation);

        ChatMessage userMessage = new ChatMessage();
        userMessage.setConversation(savedConversation);
        userMessage.setRole("USER");
        userMessage.setContent("What is the WFH policy?");

        ChatMessage assistantMessage = new ChatMessage();
        assistantMessage.setConversation(savedConversation);
        assistantMessage.setRole("ASSISTANT");
        assistantMessage.setContent("Employees can work from home.");

        chatMessageRepository.save(userMessage);
        chatMessageRepository.save(assistantMessage);

        List<ChatMessage> result =
                chatMessageRepository
                        .findByConversationIdOrderByCreatedAtAsc(
                                savedConversation.getId()
                        );

        assertEquals(2, result.size());

        assertEquals("USER", result.get(0).getRole());
        assertEquals(
                "What is the WFH policy?",
                result.get(0).getContent()
        );

        assertEquals("ASSISTANT", result.get(1).getRole());
        assertEquals(
                "Employees can work from home.",
                result.get(1).getContent()
        );

        assertTrue(
                result.get(0).getCreatedAt()
                        .compareTo(result.get(1).getCreatedAt()) <= 0
        );
    }

    @TestConfiguration
    static class TestCacheConfig {

        @Bean
        CacheManager cacheManager() {
            return new ConcurrentMapCacheManager("aiAnswers");
        }
    }
}