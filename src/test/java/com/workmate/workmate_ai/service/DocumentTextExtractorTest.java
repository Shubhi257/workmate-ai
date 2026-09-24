package com.workmate.workmate_ai.service;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

class DocumentTextExtractorTest {

    @Test
    void extractText_shouldExtractTextFromInputStream() throws Exception {

        // Arrange
        DocumentTextExtractor extractor =
                new DocumentTextExtractor();

        String expectedText =
                "WorkMate AI is an enterprise knowledge assistant.";

        ByteArrayInputStream inputStream =
                new ByteArrayInputStream(
                        expectedText.getBytes(StandardCharsets.UTF_8)
                );

        // Act
        String extractedText =
                extractor.extractText(inputStream);

        // Assert
        assertNotNull(extractedText);
        assertTrue(extractedText.contains(expectedText));
    }
}