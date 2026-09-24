package com.workmate.workmate_ai.repository;

import com.workmate.workmate_ai.entity.Document;
import com.workmate.workmate_ai.entity.DocumentChunk;
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
@Import(DocumentChunkRepositoryTest.TestCacheConfig.class)
class DocumentChunkRepositoryTest {

    @Autowired
    private DocumentChunkRepository documentChunkRepository;

    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void findByDocumentIdOrderByChunkIndex_shouldReturnChunksInOrder() {

        User user = new User();
        user.setName("Test User");
        user.setEmail("chunk@test.com");
        user.setPassword("hashedPassword");

        User savedUser = userRepository.save(user);

        Document document = new Document();
        document.setTitle("Employee WFH Policy");
        document.setFileName("wfh-policy.pdf");
        document.setFileType("application/pdf");
        document.setUser(savedUser);

        Document savedDocument = documentRepository.save(document);

        DocumentChunk chunk1 = new DocumentChunk();
        chunk1.setChunkIndex(0);
        chunk1.setContent("Employees can work from home.");
        chunk1.setDocument(savedDocument);

        DocumentChunk chunk2 = new DocumentChunk();
        chunk2.setChunkIndex(1);
        chunk2.setContent("Approval from the manager is required.");
        chunk2.setDocument(savedDocument);

        DocumentChunk chunk3 = new DocumentChunk();
        chunk3.setChunkIndex(2);
        chunk3.setContent("Employees must follow the WFH policy.");
        chunk3.setDocument(savedDocument);

        documentChunkRepository.save(chunk2);
        documentChunkRepository.save(chunk3);
        documentChunkRepository.save(chunk1);

        List<DocumentChunk> result =
                documentChunkRepository
                        .findByDocumentIdOrderByChunkIndex(savedDocument.getId());

        assertEquals(3, result.size());

        assertEquals(0, result.get(0).getChunkIndex());
        assertEquals(1, result.get(1).getChunkIndex());
        assertEquals(2, result.get(2).getChunkIndex());

        assertEquals(
                "Employees can work from home.",
                result.get(0).getContent()
        );

        assertEquals(
                "Approval from the manager is required.",
                result.get(1).getContent()
        );

        assertEquals(
                "Employees must follow the WFH policy.",
                result.get(2).getContent()
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