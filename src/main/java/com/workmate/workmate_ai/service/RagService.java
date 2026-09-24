package com.workmate.workmate_ai.service;

import com.workmate.workmate_ai.dto.ChatResponse;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RagService {

    private final ChatClient chatClient;
    private final VectorStoreService vectorStoreService;

    public RagService(ChatClient.Builder chatClientBuilder,
                      VectorStoreService vectorStoreService) {

        this.chatClient = chatClientBuilder.build();
        this.vectorStoreService = vectorStoreService;
    }

    public ChatResponse ask(String question) {
        return ask(question, "");
    }

    public ChatResponse ask(String question, String conversationHistory) {

        /*
         * Use conversation history when performing vector search.
         * This helps resolve questions like:
         *
         * Previous: How many WFH days are allowed?
         * Current:  Who approves it?
         *
         * The search query becomes contextual instead of just "Who approves it?"
         */
        String retrievalQuery = question;

        if (conversationHistory != null
                && !conversationHistory.isBlank()) {

            retrievalQuery = """
                    Previous conversation:
                    %s

                    Current question:
                    %s
                    """.formatted(conversationHistory, question);
        }

        List<Document> relevantDocuments =
                vectorStoreService.search(retrievalQuery);

        String context = relevantDocuments.stream()
                .map(Document::getText)
                .reduce("", (a, b) -> a + "\n\n---\n\n" + b);

        String prompt = """
                You are WorkMate AI, an enterprise knowledge assistant.

                Answer the user's current question using ONLY the provided
                document context.

                IMPORTANT RULES:
                1. Answer the exact current question directly.
                2. Use previous conversation only to understand references
                   such as "it", "that", or "this".
                3. Document context is the source of truth.
                4. Do not use conversation history as factual evidence.
                5. Do not confuse eligibility requirements with approval
                   requirements.
                6. If the context contains an exact statement answering
                   the question, use that statement.
                7. Do not add unrelated information.
                8. Do not make assumptions or invent information.
                9. Keep the answer concise, usually 1-3 sentences.
                10. If the answer is not present in the document context,
                    say:
                    "I could not find this information in the available documents."

                Previous conversation:
                %s

                Document context:
                %s

                Current user question:
                %s

                Answer:
                """.formatted(
                conversationHistory,
                context,
                question
        );

        String answer = chatClient
                .prompt()
                .user(prompt)
                .call()
                .content();

        List<ChatResponse.Source> sources = relevantDocuments.stream()
                .map(document -> {

                    Object documentId =
                            document.getMetadata().get("documentId");

                    Object fileName =
                            document.getMetadata().get("fileName");

                    Object chunkIndex =
                            document.getMetadata().get("chunkIndex");

                    return new ChatResponse.Source(
                            documentId instanceof Number
                                    ? ((Number) documentId).longValue()
                                    : null,

                            fileName != null
                                    ? fileName.toString()
                                    : null,

                            chunkIndex instanceof Number
                                    ? ((Number) chunkIndex).intValue()
                                    : null
                    );
                })
                .toList();

        return new ChatResponse(answer, sources);
    }
}