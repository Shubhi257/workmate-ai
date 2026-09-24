package com.workmate.workmate_ai.service;

import com.workmate.workmate_ai.entity.DocumentChunk;
import com.workmate.workmate_ai.repository.DocumentChunkRepository;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class VectorStoreService {

    private final VectorStore vectorStore;
    private final DocumentChunkRepository documentChunkRepository;

    public VectorStoreService(VectorStore vectorStore,
                              DocumentChunkRepository documentChunkRepository) {
        this.vectorStore = vectorStore;
        this.documentChunkRepository = documentChunkRepository;
    }

    public void indexDocumentChunks(Long documentId) {

        List<DocumentChunk> chunks =
                documentChunkRepository.findByDocumentIdOrderByChunkIndex(documentId);

        List<Document> documents = chunks.stream()
                .map(chunk -> {

                    Map<String, Object> metadata = new HashMap<>();
                    metadata.put("documentId", chunk.getDocument().getId());
                    metadata.put("chunkId", chunk.getId());
                    metadata.put("chunkIndex", chunk.getChunkIndex());
                    metadata.put("fileName", chunk.getDocument().getFileName());

                    return new Document(
                            chunk.getContent(),
                            metadata
                    );
                })
                .toList();

        vectorStore.add(documents);
    }

    public List<Document> search(String question) {

        return vectorStore.similaritySearch(
                org.springframework.ai.vectorstore.SearchRequest
                        .builder()
                        .query(question)
                        .topK(3)
                        .build()
        );
    }
}