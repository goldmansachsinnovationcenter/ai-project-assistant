package com.example.springai.entity;

import com.example.springai.model.search.SearchQuery.QueryIntent;
import jakarta.persistence.*;
import java.util.List;

/**
 * Enhanced Query entity for financial research queries
 * Supports queries like:
 * - "David kostin's view on the s&p 500"
 * - "US gdp"
 * - "what is apple projected q2 earnings"
 */
@Entity
@Table(name = "financial_queries")
public class FinancialQuery extends Query {
    
    @Column(name = "query_intent")
    @Enumerated(EnumType.STRING)
    private QueryIntent intent;
    
    @ElementCollection
    @CollectionTable(name = "financial_query_entities", joinColumns = @JoinColumn(name = "query_id"))
    @Column(name = "entity")
    private List<String> entities;
    
    @Column(name = "time_period")
    private String timePeriod;
    
    @Column(name = "start_date")
    private String startDate;
    
    @Column(name = "end_date")
    private String endDate;
    
    @Column(name = "report_type")
    private String reportType;
    
    @ElementCollection
    @CollectionTable(name = "financial_query_keywords", joinColumns = @JoinColumn(name = "query_id"))
    @Column(name = "keyword")
    private List<String> keywords;
    
    @Column(name = "analyst_name")
    private String analystName;
    
    @Column(name = "company_name")
    private String companyName;
    
    @Column(name = "indicator_name")
    private String indicatorName;
    
    @Column(name = "parsed_query", columnDefinition = "TEXT")
    private String parsedQuery;

    public QueryIntent getIntent() {
        return intent;
    }

    public void setIntent(QueryIntent intent) {
        this.intent = intent;
    }

    public List<String> getEntities() {
        return entities;
    }

    public void setEntities(List<String> entities) {
        this.entities = entities;
    }

    public String getTimePeriod() {
        return timePeriod;
    }

    public void setTimePeriod(String timePeriod) {
        this.timePeriod = timePeriod;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getReportType() {
        return reportType;
    }

    public void setReportType(String reportType) {
        this.reportType = reportType;
    }

    public List<String> getKeywords() {
        return keywords;
    }

    public void setKeywords(List<String> keywords) {
        this.keywords = keywords;
    }

    public String getAnalystName() {
        return analystName;
    }

    public void setAnalystName(String analystName) {
        this.analystName = analystName;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getIndicatorName() {
        return indicatorName;
    }

    public void setIndicatorName(String indicatorName) {
        this.indicatorName = indicatorName;
    }

    public String getParsedQuery() {
        return parsedQuery;
    }

    public void setParsedQuery(String parsedQuery) {
        this.parsedQuery = parsedQuery;
    }
}
