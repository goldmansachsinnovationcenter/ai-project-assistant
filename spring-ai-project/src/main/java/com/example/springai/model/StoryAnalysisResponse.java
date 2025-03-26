package com.example.springai.model;

import java.util.List;

public class StoryAnalysisResponse {
    private List<String> stories;
    private List<String> queries;
    private List<String> risks;
    private List<String> nfrs;
    private String summary;

    public List<String> getStories() {
        return stories;
    }

    public void setStories(List<String> stories) {
        this.stories = stories;
    }

    public List<String> getQueries() {
        return queries;
    }

    public void setQueries(List<String> queries) {
        this.queries = queries;
    }

    public List<String> getRisks() {
        return risks;
    }

    public void setRisks(List<String> risks) {
        this.risks = risks;
    }

    public List<String> getNfrs() {
        return nfrs;
    }

    public void setNfrs(List<String> nfrs) {
        this.nfrs = nfrs;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }
}
