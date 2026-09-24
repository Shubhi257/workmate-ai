package com.workmate.workmate_ai.repository;

import com.workmate.workmate_ai.entity.Document;
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
@Import(DocumentRepositoryTest.TestCacheConfig.class)
class DocumentRepositoryTest {

    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void save_shouldStoreDocumentSuccessfully() {

        User user = new User();
        user.setName("Test User");
        user.setEmail("document@test.com");
        user.setPassword("hashedPassword");

        User savedUser = userRepository.save(user);

        Document document = new Document();
        document.setTitle("Employee WFH Policy");
        document.setFileName("wfh-policy.pdf");
        document.setFileType("application/pdf");
        document.setUser(savedUser);

        Document savedDocument = documentRepository.save(document);

        assertNotNull(savedDocument.getId());
        assertEquals("Employee WFH Policy", savedDocument.getTitle());
        assertEquals("wfh-policy.pdf", savedDocument.getFileName());
        assertEquals("application/pdf", savedDocument.getFileType());
        assertEquals(savedUser.getId(), savedDocument.getUser().getId());
    }

    @Test
    void findById_shouldReturnDocument_whenDocumentExists() {

        User user = new User();
        user.setName("Test User");
        user.setEmail("document2@test.com");
        user.setPassword("hashedPassword");

        User savedUser = userRepository.save(user);

        Document document = new Document();
        document.setTitle("Leave Policy");
        document.setFileName("leave-policy.pdf");
        document.setFileType("application/pdf");
        document.setUser(savedUser);

        Document savedDocument = documentRepository.save(document);

        Optional<Document> result =
                documentRepository.findById(savedDocument.getId());

        assertTrue(result.isPresent());
        assertEquals("Leave Policy", result.get().getTitle());
        assertEquals("leave-policy.pdf", result.get().getFileName());
        assertEquals(savedUser.getId(), result.get().getUser().getId());
    }

    @TestConfiguration
    static class TestCacheConfig {

        @Bean
        CacheManager cacheManager() {
            return new ConcurrentMapCacheManager("aiAnswers");
        }
    }
}