package com.example.springai.repository;

import com.example.springai.entity.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ConversationRepository extends JpaRepository<Conversation, String> {
    
    List<Conversation> findByUserIdOrderByLastActivityDesc(String userId);
    
    Optional<Conversation> findByUserIdAndId(String userId, String id);
    
    @Query("SELECT c FROM Conversation c WHERE c.userId = :userId AND c.lastActivity >= :since ORDER BY c.lastActivity DESC")
    List<Conversation> findRecentConversationsByUser(@Param("userId") String userId, @Param("since") LocalDateTime since);
    
    @Query("SELECT COUNT(c) FROM Conversation c WHERE c.userId = :userId")
    long countByUserId(@Param("userId") String userId);
}
