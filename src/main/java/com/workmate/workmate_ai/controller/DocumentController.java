package com.workmate.workmate_ai.controller;

import com.workmate.workmate_ai.dto.DocumentResponse;
import com.workmate.workmate_ai.entity.Document;
import com.workmate.workmate_ai.service.DocumentService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/documents")
public class DocumentController {

    private final DocumentService documentService;

    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }

    @PostMapping
    public DocumentResponse createDocument(
            @RequestBody Document document) {

        return documentService.createDocument(document);
    }

    @GetMapping
    public List<DocumentResponse> getAllDocuments() {

        return documentService.getAllDocuments();
    }

    @PostMapping("/upload")
    public DocumentResponse uploadDocument(
            @RequestParam("title") String title,
            @RequestParam("file") MultipartFile file)
            throws Exception {

        return documentService.uploadDocument(title, file);
    }
}