package com.workmate.workmate_ai.repository;

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

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Import(ConversationRepositoryTest.TestCacheConfig.class)
class ConversationRepositoryTest {

    @Autowired
    private ConversationRepository conversationRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void save_shouldStoreConversationSuccessfully() {

        User user = new User();
        user.setName("Test User");
        user.setEmail("conversation@test.com");
        user.setPassword("hashedPassword");

        User savedUser = userRepository.save(user);

        Conversation conversation = new Conversation();
        conversation.setUser(savedUser);

        Conversation savedConversation =
                conversationRepository.save(conversation);

        assertNotNull(savedConversation.getId());
        assertNotNull(savedConversation.getUser());
        assertEquals(savedUser.getId(), savedConversation.getUser().getId());
    }

    @Test
    void findById_shouldReturnConversation_whenConversationExists() {

        User user = new User();
        user.setName("Test User");
        user.setEmail("conversation2@test.com");
        user.setPassword("hashedPassword");

        User savedUser = userRepository.save(user);

        Conversation conversation = new Conversation();
        conversation.setUser(savedUser);

        Conversation savedConversation =
                conversationRepository.save(conversation);

        Optional<Conversation> result =
                conversationRepository.findById(savedConversation.getId());

        assertTrue(result.isPresent());
        assertEquals(
                savedUser.getId(),
                result.get().getUser().getId()
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