package com.example.springai.repository;

import com.example.springai.entity.ChatMessage;
import com.example.springai.controller.ChatController; // Needed for context config exclusion
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.ai.ollama.OllamaChatModel; // Mock this as it's a dependency of ChatController

import org.junit.jupiter.api.BeforeEach;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Comparator;


import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest(excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = ChatController.class))
class ChatMessageRepositoryTest {

    @Autowired
    private TestEntityManager entityManager; // Provides helper methods for JPA testing

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @MockBean
    private OllamaChatModel ollamaChatModel;

    private ChatMessage msg1, msg2, msg3, msg4;

    @BeforeEach
    void setUp() {
        msg1 = new ChatMessage("User prompt 1", "Oldest Response");
        msg1.setTimestamp(LocalDateTime.now().minusDays(1)); // Explicitly set timestamp for ordering

        msg2 = new ChatMessage("User prompt 2", "Middle 1 Response");
        msg2.setTimestamp(LocalDateTime.now().minusHours(5));

        msg3 = new ChatMessage("User prompt 3", "Middle 2 Response");
        msg3.setTimestamp(LocalDateTime.now().minusHours(1));

        msg4 = new ChatMessage("User prompt 4", "Newest Response");
        msg4.setTimestamp(LocalDateTime.now());

        entityManager.persist(msg1);
        entityManager.persist(msg2);
        entityManager.persist(msg3);
        entityManager.persist(msg4);
        entityManager.flush(); // Ensure data is written to the H2 database
    }

    @Test
    void findRecentMessages_returnsAllMessagesOrderedByTimestampDesc() {
        List<ChatMessage> recentMessages = chatMessageRepository.findRecentMessages();

        assertThat(recentMessages).hasSize(4);
        assertThat(recentMessages).isSortedAccordingTo(Comparator.comparing(ChatMessage::getTimestamp).reversed());
        assertThat(recentMessages).extracting(ChatMessage::getResponse)
                .containsExactly("Newest Response", "Middle 2 Response", "Middle 1 Response", "Oldest Response");
    }

    @Test
    void findRecentMessages_withLimit_returnsLimitedMessagesOrderedByTimestampDesc() {
        List<ChatMessage> recentMessagesLimited = chatMessageRepository.findRecentMessages(2);

        assertThat(recentMessagesLimited).hasSize(2);
        assertThat(recentMessagesLimited).isSortedAccordingTo(Comparator.comparing(ChatMessage::getTimestamp).reversed());
        assertThat(recentMessagesLimited).extracting(ChatMessage::getResponse)
                .containsExactly("Newest Response", "Middle 2 Response"); // Only the 2 newest
    }

    @Test
    void findRecentMessages_withLimitLargerThanTotal_returnsAllMessagesOrderedByTimestampDesc() {
        List<ChatMessage> recentMessagesLimited = chatMessageRepository.findRecentMessages(10); // Limit > total messages

        assertThat(recentMessagesLimited).hasSize(4); // Should return all messages
        assertThat(recentMessagesLimited).isSortedAccordingTo(Comparator.comparing(ChatMessage::getTimestamp).reversed());
        assertThat(recentMessagesLimited).extracting(ChatMessage::getResponse)
                .containsExactly("Newest Response", "Middle 2 Response", "Middle 1 Response", "Oldest Response");
    }

    @Test
    void findRecentMessages_withZeroLimit_returnsEmptyList() {
        List<ChatMessage> recentMessagesLimited = chatMessageRepository.findRecentMessages(0);

        assertThat(recentMessagesLimited).isEmpty(); // Expect an empty list
    }

     @Test
    void findRecentMessages_withNegativeLimit_throwsException() {
         assertThrows(Exception.class, () -> {
             chatMessageRepository.findRecentMessages(-5);
         }, "Should throw an exception for negative limit");
    }


    @Test
    void findRecentMessages_whenRepositoryIsEmpty_returnsEmptyList() {
        chatMessageRepository.deleteAll();
        entityManager.flush(); // Ensure deletion is committed
        entityManager.clear(); // Detach all entities

        List<ChatMessage> recentMessages = chatMessageRepository.findRecentMessages();
        List<ChatMessage> recentMessagesLimited = chatMessageRepository.findRecentMessages(5);

        assertThat(recentMessages).isEmpty();
        assertThat(recentMessagesLimited).isEmpty();
    }

}
