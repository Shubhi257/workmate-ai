package com.workmate.workmate_ai.repository;

import com.workmate.workmate_ai.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Import(UserRepositoryTest.TestCacheConfig.class)
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void findByEmail_shouldReturnUser_whenEmailExists() {

        User user = new User();
        user.setName("Test User");
        user.setEmail("repository@test.com");
        user.setPassword("hashedPassword");

        userRepository.save(user);

        Optional<User> result =
                userRepository.findByEmail("repository@test.com");

        assertTrue(result.isPresent());
        assertEquals("Test User", result.get().getName());
        assertEquals("repository@test.com", result.get().getEmail());
    }

    @TestConfiguration
    static class TestCacheConfig {

        @Bean
        CacheManager cacheManager() {
            return new ConcurrentMapCacheManager("aiAnswers");
        }
    }
}