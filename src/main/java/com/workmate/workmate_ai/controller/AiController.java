package com.workmate.workmate_ai.controller;

import com.workmate.workmate_ai.service.AiService;
import com.workmate.workmate_ai.service.EmbeddingService;
import com.workmate.workmate_ai.service.VectorStoreService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import org.springframework.ai.document.Document;

@RestController
@RequestMapping("/api/ai")
public class AiController {

    private final AiService aiService;
    private final VectorStoreService vectorStoreService;

    public AiController(AiService aiService,
                        VectorStoreService vectorStoreService) {

        this.aiService = aiService;
        this.vectorStoreService = vectorStoreService;
    }

    @PostMapping("/ask")
    public String ask(@RequestBody Map<String, String> request) {

        String question = request.get("question");

        return aiService.ask(question);
    }

    @PostMapping("/index/{documentId}")
    public String indexDocument(@PathVariable Long documentId) {

        vectorStoreService.indexDocumentChunks(documentId);

        return "Document indexed successfully";
    }

    @GetMapping("/search")
    public List<String> search(@RequestParam String question) {

        return vectorStoreService.search(question)
                .stream()
                .map(Document::getText)
                .toList();
    }

    @GetMapping("/embedding-test")
    public String embeddingTest() {

        EmbeddingService embeddingService =
                new EmbeddingService(null);

        return "Embedding test endpoint";
    }
}