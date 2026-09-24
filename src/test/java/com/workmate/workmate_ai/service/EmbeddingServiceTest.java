package com.workmate.workmate_ai.service;

import org.junit.jupiter.api.Test;
import org.springframework.ai.embedding.EmbeddingModel;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EmbeddingServiceTest {

    @Test
    void generateEmbedding_shouldReturnEmbeddingFromModel() {

        // Arrange
        EmbeddingModel embeddingModel = mock(EmbeddingModel.class);

        EmbeddingService embeddingService =
                new EmbeddingService(embeddingModel);

        float[] expectedEmbedding = new float[]{0.1f, 0.2f, 0.3f};

        when(embeddingModel.embed("WorkMate AI"))
                .thenReturn(expectedEmbedding);

        // Act
        float[] actualEmbedding =
                embeddingService.generateEmbedding("WorkMate AI");

        // Assert
        assertNotNull(actualEmbedding);
        assertArrayEquals(expectedEmbedding, actualEmbedding);

        verify(embeddingModel).embed("WorkMate AI");
    }
}