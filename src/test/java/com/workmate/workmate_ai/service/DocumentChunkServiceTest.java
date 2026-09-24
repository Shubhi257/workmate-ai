package com.workmate.workmate_ai.service;

import com.workmate.workmate_ai.entity.Document;
import com.workmate.workmate_ai.entity.DocumentChunk;
import com.workmate.workmate_ai.repository.DocumentChunkRepository;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

class DocumentChunkServiceTest {

    @Test
    void createChunks_shouldSplitLongDocumentIntoChunks() {

        // Arrange
        DocumentChunkRepository repository =
                mock(DocumentChunkRepository.class);

        DocumentChunkService service =
                new DocumentChunkService(repository);

        String text = "A".repeat(2500);

        Document document = new Document();
        document.setId(1L);
        document.setTitle("Test Document");
        document.setExtractedText(text);

        when(repository.saveAll(anyList()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        List<DocumentChunk> chunks =
                service.createChunks(document);

        // Assert
        assertEquals(3, chunks.size());

        assertEquals(0, chunks.get(0).getChunkIndex());
        assertEquals(1, chunks.get(1).getChunkIndex());
        assertEquals(2, chunks.get(2).getChunkIndex());

        for (DocumentChunk chunk : chunks) {
            assertEquals(document, chunk.getDocument());
            assertFalse(chunk.getContent().isBlank());
        }

        verify(repository).saveAll(anyList());
    }

    @Test
    void createChunks_shouldReturnEmptyList_whenDocumentTextIsEmpty() {

        // Arrange
        DocumentChunkRepository repository =
                mock(DocumentChunkRepository.class);

        DocumentChunkService service =
                new DocumentChunkService(repository);

        Document document = new Document();
        document.setId(1L);
        document.setTitle("Empty Document");
        document.setExtractedText("");

        // Act
        List<DocumentChunk> chunks =
                service.createChunks(document);

        // Assert
        assertTrue(chunks.isEmpty());

        verify(repository, never()).saveAll(anyList());
    }
}