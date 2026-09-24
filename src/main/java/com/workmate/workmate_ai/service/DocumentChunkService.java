package com.workmate.workmate_ai.service;

import com.workmate.workmate_ai.entity.Document;
import com.workmate.workmate_ai.entity.DocumentChunk;
import com.workmate.workmate_ai.repository.DocumentChunkRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class DocumentChunkService {

    private static final int CHUNK_SIZE = 1000;
    private static final int CHUNK_OVERLAP = 200;

    private final DocumentChunkRepository documentChunkRepository;

    public DocumentChunkService(DocumentChunkRepository documentChunkRepository) {
        this.documentChunkRepository = documentChunkRepository;
    }

    public List<DocumentChunk> createChunks(Document document) {

        String text = document.getExtractedText();

        if (text == null || text.isBlank()) {
            return List.of();
        }

        List<DocumentChunk> chunks = new ArrayList<>();

        int start = 0;
        int chunkIndex = 0;

        while (start < text.length()) {

            int end = Math.min(
                    start + CHUNK_SIZE,
                    text.length()
            );

            String chunkContent = text.substring(start, end).trim();

            if (!chunkContent.isBlank()) {

                DocumentChunk chunk = new DocumentChunk();

                chunk.setChunkIndex(chunkIndex);
                chunk.setContent(chunkContent);
                chunk.setDocument(document);

                chunks.add(chunk);

                chunkIndex++;
            }

            if (end == text.length()) {
                break;
            }

            start = end - CHUNK_OVERLAP;
        }

        return documentChunkRepository.saveAll(chunks);
    }
}