package com.workmate.workmate_ai.service;

import com.workmate.workmate_ai.dto.ChatApiResponse;
import com.workmate.workmate_ai.dto.ChatMessageResponse;
import com.workmate.workmate_ai.dto.ConversationResponse;
import com.workmate.workmate_ai.entity.ChatMessage;
import com.workmate.workmate_ai.entity.Conversation;
import com.workmate.workmate_ai.entity.User;
import com.workmate.workmate_ai.repository.ChatMessageRepository;
import com.workmate.workmate_ai.repository.ConversationRepository;
import com.workmate.workmate_ai.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChatService {

    private final ConversationRepository conversationRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final UserRepository userRepository;
    private final RagService ragService;

    public ChatService(ConversationRepository conversationRepository,
                       ChatMessageRepository chatMessageRepository,
                       UserRepository userRepository,
                       RagService ragService) {

        this.conversationRepository = conversationRepository;
        this.chatMessageRepository = chatMessageRepository;
        this.userRepository = userRepository;
        this.ragService = ragService;
    }

    public ChatApiResponse chat(Long conversationId, String question) {

        String email = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Conversation conversation;

        if (conversationId == null) {

            conversation = new Conversation();
            conversation.setUser(user);

            conversation = conversationRepository.save(conversation);

        } else {

            conversation = conversationRepository.findById(conversationId)
                    .orElseThrow(() ->
                            new RuntimeException("Conversation not found"));

            if (!conversation.getUser().getId().equals(user.getId())) {
                throw new RuntimeException(
                        "You do not have access to this conversation"
                );
            }
        }

        List<ChatMessage> previousMessages =
                chatMessageRepository
                        .findByConversationIdOrderByCreatedAtAsc(
                                conversation.getId()
                        );

        String conversationHistory = previousMessages.stream()
                .map(message ->
                        message.getRole() + ": " + message.getContent()
                )
                .reduce("", (a, b) -> a + "\n" + b);

        ChatMessage userMessage = new ChatMessage();
        userMessage.setConversation(conversation);
        userMessage.setRole("USER");
        userMessage.setContent(question);

        chatMessageRepository.save(userMessage);

        String answer = ragService
                .ask(question, conversationHistory)
                .getAnswer();

        ChatMessage assistantMessage = new ChatMessage();
        assistantMessage.setConversation(conversation);
        assistantMessage.setRole("ASSISTANT");
        assistantMessage.setContent(answer);

        chatMessageRepository.save(assistantMessage);

        return new ChatApiResponse(
                conversation.getId(),
                answer
        );
    }

    public List<ConversationResponse> getConversations() {

        String email = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return conversationRepository
                .findByUserIdOrderByUpdatedAtDesc(user.getId())
                .stream()
                .map(conversation ->
                        new ConversationResponse(
                                conversation.getId(),
                                conversation.getCreatedAt(),
                                conversation.getUpdatedAt()
                        )
                )
                .toList();
    }

    public List<ChatMessageResponse> getMessages(Long conversationId) {

        String email = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Conversation conversation =
                conversationRepository.findById(conversationId)
                        .orElseThrow(() ->
                                new RuntimeException("Conversation not found"));

        if (!conversation.getUser().getId().equals(user.getId())) {
            throw new RuntimeException(
                    "You do not have access to this conversation"
            );
        }

        return chatMessageRepository
                .findByConversationIdOrderByCreatedAtAsc(conversationId)
                .stream()
                .map(message ->
                        new ChatMessageResponse(
                                message.getId(),
                                message.getRole(),
                                message.getContent(),
                                message.getCreatedAt()
                        )
                )
                .toList();
    }
}