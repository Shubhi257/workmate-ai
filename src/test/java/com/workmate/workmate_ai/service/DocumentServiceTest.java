package com.workmate.workmate_ai.service;

import com.workmate.workmate_ai.repository.DocumentRepository;
import com.workmate.workmate_ai.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class DocumentServiceTest {

    @Test
    void uploadDocument_shouldRejectEmptyFile() throws Exception {

        // Arrange
        DocumentRepository documentRepository =
                mock(DocumentRepository.class);

        UserRepository userRepository =
                mock(UserRepository.class);

        DocumentTextExtractor documentTextExtractor =
                mock(DocumentTextExtractor.class);

        DocumentChunkService documentChunkService =
                mock(DocumentChunkService.class);

        VectorStoreService vectorStoreService =
                mock(VectorStoreService.class);

        DocumentService documentService =
                new DocumentService(
                        documentRepository,
                        userRepository,
                        documentTextExtractor,
                        documentChunkService,
                        vectorStoreService
                );

        MultipartFile emptyFile =
                new MockMultipartFile(
                        "file",
                        "test.pdf",
                        "application/pdf",
                        new byte[0]
                );

        // Act + Assert
        assertThrows(
                IllegalArgumentException.class,
                () -> documentService.uploadDocument(
                        "Test Document",
                        emptyFile
                )
        );

        // Make sure no document was saved
        verify(documentRepository, never()).save(any());
    }

    @Test
    void uploadDocument_shouldRejectUnsupportedFileType() throws Exception {

        // Arrange
        DocumentRepository documentRepository =
                mock(DocumentRepository.class);

        UserRepository userRepository =
                mock(UserRepository.class);

        DocumentTextExtractor documentTextExtractor =
                mock(DocumentTextExtractor.class);

        DocumentChunkService documentChunkService =
                mock(DocumentChunkService.class);

        VectorStoreService vectorStoreService =
                mock(VectorStoreService.class);

        DocumentService documentService =
                new DocumentService(
                        documentRepository,
                        userRepository,
                        documentTextExtractor,
                        documentChunkService,
                        vectorStoreService
                );

        MultipartFile imageFile =
                new MockMultipartFile(
                        "file",
                        "test.jpg",
                        "image/jpeg",
                        "fake image content".getBytes()
                );

        // Act + Assert
        assertThrows(
                IllegalArgumentException.class,
                () -> documentService.uploadDocument(
                        "Test Image",
                        imageFile
                )
        );

        // File should be rejected before saving
        verify(documentRepository, never()).save(any());
    }
}