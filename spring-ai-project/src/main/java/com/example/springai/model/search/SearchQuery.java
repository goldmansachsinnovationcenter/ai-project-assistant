package com.example.springai.model.search;

import java.util.List;

/**
 * Represents a search query with extracted parameters from user input
 */
public class SearchQuery {
    private String originalQuery;
    private QueryIntent intent;
    private List<String> entities;
    private TimeFrame timeFrame;
    private String reportType;
    private List<String> keywords;

    public enum QueryIntent {
        MARKET_DATA,
        ECONOMIC_INDICATOR,
        ANALYST_REPORT,
        EARNINGS_PROJECTION,
        GENERAL_INFORMATION
    }

    public static class TimeFrame {
        private String startDate;
        private String endDate;
        private String period; // e.g., "Q2", "2023", "last 6 months"

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

        public String getPeriod() {
            return period;
        }

        public void setPeriod(String period) {
            this.period = period;
        }
    }

    public String getOriginalQuery() {
        return originalQuery;
    }

    public void setOriginalQuery(String originalQuery) {
        this.originalQuery = originalQuery;
    }

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

    public TimeFrame getTimeFrame() {
        return timeFrame;
    }

    public void setTimeFrame(TimeFrame timeFrame) {
        this.timeFrame = timeFrame;
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
}
