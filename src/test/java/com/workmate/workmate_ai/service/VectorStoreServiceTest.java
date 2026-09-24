package com.workmate.workmate_ai.service;

import com.workmate.workmate_ai.entity.Document;
import com.workmate.workmate_ai.entity.DocumentChunk;
import com.workmate.workmate_ai.repository.DocumentChunkRepository;
import org.junit.jupiter.api.Test;
import org.springframework.ai.vectorstore.VectorStore;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

class VectorStoreServiceTest {

    @Test
    void indexDocumentChunks_shouldAddChunksToVectorStore() {

        // Arrange
        VectorStore vectorStore = mock(VectorStore.class);

        DocumentChunkRepository repository =
                mock(DocumentChunkRepository.class);

        VectorStoreService service =
                new VectorStoreService(vectorStore, repository);

        Document document = new Document();
        document.setId(1L);
        document.setFileName("wfh-policy.pdf");

        DocumentChunk chunk1 = new DocumentChunk();
        chunk1.setId(101L);
        chunk1.setChunkIndex(0);
        chunk1.setContent("Employees can work from home two days per week.");
        chunk1.setDocument(document);

        DocumentChunk chunk2 = new DocumentChunk();
        chunk2.setId(102L);
        chunk2.setChunkIndex(1);
        chunk2.setContent("Manager approval is required.");
        chunk2.setDocument(document);

        when(repository.findByDocumentIdOrderByChunkIndex(1L))
                .thenReturn(List.of(chunk1, chunk2));

        // Act
        service.indexDocumentChunks(1L);

        // Assert
        verify(repository)
                .findByDocumentIdOrderByChunkIndex(1L);

        verify(vectorStore)
                .add(anyList());
    }
}