package com.workmate.workmate_ai.service;

import com.workmate.workmate_ai.dto.DocumentResponse;
import com.workmate.workmate_ai.entity.Document;
import com.workmate.workmate_ai.entity.User;
import com.workmate.workmate_ai.repository.DocumentRepository;
import com.workmate.workmate_ai.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.cache.annotation.CacheEvict;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Service
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final UserRepository userRepository;
    private final DocumentTextExtractor documentTextExtractor;
    private final DocumentChunkService documentChunkService;
    private final VectorStoreService vectorStoreService;

    public DocumentService(DocumentRepository documentRepository,
                           UserRepository userRepository,
                           DocumentTextExtractor documentTextExtractor,
                           DocumentChunkService documentChunkService,
                           VectorStoreService vectorStoreService) {

        this.documentRepository = documentRepository;
        this.userRepository = userRepository;
        this.documentTextExtractor = documentTextExtractor;
        this.documentChunkService = documentChunkService;
        this.vectorStoreService = vectorStoreService;
    }

    public DocumentResponse createDocument(Document document) {

        String email = getCurrentUserEmail();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        document.setUser(user);

        Document savedDocument = documentRepository.save(document);

        documentChunkService.createChunks(savedDocument);

        return toResponse(savedDocument);
    }

    public List<DocumentResponse> getAllDocuments() {

        String email = getCurrentUserEmail();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        return documentRepository.findByUserId(user.getId())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public String getCurrentUserEmail() {

        return SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();
    }

    @CacheEvict(value = "aiAnswers", allEntries = true)
    public DocumentResponse uploadDocument(String title, MultipartFile file)
            throws Exception {

        // 1. Validate that the file is not empty
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File cannot be empty");
        }

        // 2. Get and validate the original file name
        String originalFileName = file.getOriginalFilename();

        if (originalFileName == null || originalFileName.isBlank()) {
            throw new IllegalArgumentException("Invalid file name");
        }

        // 3. Remove any path information from the file name
        String originalName = Paths.get(originalFileName)
                .getFileName()
                .toString();

        // 4. Generate a unique file name
        String fileName = java.util.UUID.randomUUID()
                + "_" + originalName;

        // 5. Get the file content type
        String contentType = file.getContentType();

        // 6. Validate file type
        if (!"application/pdf".equals(contentType)
                && !"application/vnd.openxmlformats-officedocument.wordprocessingml.document".equals(contentType)
                && !"text/plain".equals(contentType)) {

            throw new IllegalArgumentException(
                    "Only PDF, DOCX and TXT files are allowed"
            );
        }

        // 7. Create the uploads folder if it doesn't exist
        Path uploadDirectory = Paths.get("uploads");

        if (!Files.exists(uploadDirectory)) {
            Files.createDirectories(uploadDirectory);
        }

        // 8. Create the complete file path
        Path filePath = uploadDirectory.resolve(fileName);

        // 9. Save the actual file
        Files.copy(file.getInputStream(), filePath);

        // 10. Extract text from the uploaded file
        String extractedText = documentTextExtractor.extractText(
                Files.newInputStream(filePath)
        );

        System.out.println("========== EXTRACTED TEXT ==========");
        System.out.println(extractedText);
        System.out.println("====================================");

        // 11. Get the currently logged-in user
        String email = getCurrentUserEmail();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        // 12. Create Document entity
        Document document = new Document();

        document.setTitle(title);
        document.setFileName(fileName);
        document.setFileType(contentType);
        document.setFilePath(filePath.toString());
        document.setUser(user);
        document.setExtractedText(extractedText);

        // 13. Save document information in PostgreSQL
        Document savedDocument = documentRepository.save(document);

        // 14. Create chunks from extracted text
        documentChunkService.createChunks(savedDocument);

        // 15. Generate embeddings and store chunks in PGVector
        vectorStoreService.indexDocumentChunks(savedDocument.getId());

        // 16. Return response
        return toResponse(savedDocument);
    }

    private DocumentResponse toResponse(Document document) {

        return new DocumentResponse(
                document.getId(),
                document.getTitle(),
                document.getFileName(),
                document.getFileType(),
                document.getFilePath()
        );
    }
}