package com.example.springai.repository;

import com.example.springai.entity.ChatbotAnalytics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ChatbotAnalyticsRepository extends JpaRepository<ChatbotAnalytics, String> {
    
    List<ChatbotAnalytics> findByConversationIdOrderByTimestampDesc(String conversationId);
    
    @Query("SELECT ca FROM ChatbotAnalytics ca WHERE ca.timestamp >= :startTime AND ca.timestamp <= :endTime ORDER BY ca.timestamp DESC")
    List<ChatbotAnalytics> findByTimestampBetween(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);
    
    @Query("SELECT ca.intent, COUNT(ca) FROM ChatbotAnalytics ca WHERE ca.timestamp >= :since GROUP BY ca.intent ORDER BY COUNT(ca) DESC")
    List<Object[]> findTopIntentsSince(@Param("since") LocalDateTime since);
    
    @Query("SELECT AVG(ca.responseTime) FROM ChatbotAnalytics ca WHERE ca.timestamp >= :since")
    Double findAverageResponseTimeSince(@Param("since") LocalDateTime since);
    
    @Query("SELECT ca.sentiment, COUNT(ca) FROM ChatbotAnalytics ca WHERE ca.timestamp >= :since GROUP BY ca.sentiment")
    List<Object[]> findSentimentDistributionSince(@Param("since") LocalDateTime since);
}
