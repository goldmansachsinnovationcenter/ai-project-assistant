package com.example.springai.model.search;

import java.util.List;

/**
 * Represents extracted parameters from a search query for use in search APIs
 */
public class SearchParameters {
    private String searchTerm;
    private List<String> entities;
    private String timeStart;
    private String timeEnd;
    private String timePeriod;
    private String reportType;
    private List<String> keywords;
    private List<String> sources;
    private Integer limit;
    private Boolean includeHistorical;

    public String getSearchTerm() {
        return searchTerm;
    }

    public void setSearchTerm(String searchTerm) {
        this.searchTerm = searchTerm;
    }

    public List<String> getEntities() {
        return entities;
    }

    public void setEntities(List<String> entities) {
        this.entities = entities;
    }

    public String getTimeStart() {
        return timeStart;
    }

    public void setTimeStart(String timeStart) {
        this.timeStart = timeStart;
    }

    public String getTimeEnd() {
        return timeEnd;
    }

    public void setTimeEnd(String timeEnd) {
        this.timeEnd = timeEnd;
    }

    public String getTimePeriod() {
        return timePeriod;
    }

    public void setTimePeriod(String timePeriod) {
        this.timePeriod = timePeriod;
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

    public List<String> getSources() {
        return sources;
    }

    public void setSources(List<String> sources) {
        this.sources = sources;
    }

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    public Boolean getIncludeHistorical() {
        return includeHistorical;
    }

    public void setIncludeHistorical(Boolean includeHistorical) {
        this.includeHistorical = includeHistorical;
    }
}
