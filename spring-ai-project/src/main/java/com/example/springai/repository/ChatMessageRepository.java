package com.example.springai.repository;

import com.example.springai.entity.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    @Query("SELECT c FROM ChatMessage c ORDER BY c.timestamp DESC")
    List<ChatMessage> findRecentMessages();
    
    default List<ChatMessage> findRecentMessages(@Param("limit") int limit) {
        return findRecentMessages().stream().limit(limit).toList();
    }
}
