package com.example.springai.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "jira")
public class JiraConfigProperties {
    
    private String baseUrl;
    private String username;
    private String apiToken;
    private String defaultProject;
    private Cache cache = new Cache();
    private RateLimit rateLimit = new RateLimit();
    
    public String getBaseUrl() {
        return baseUrl;
    }
    
    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getApiToken() {
        return apiToken;
    }
    
    public void setApiToken(String apiToken) {
        this.apiToken = apiToken;
    }
    
    public String getDefaultProject() {
        return defaultProject;
    }
    
    public void setDefaultProject(String defaultProject) {
        this.defaultProject = defaultProject;
    }
    
    public Cache getCache() {
        return cache;
    }
    
    public void setCache(Cache cache) {
        this.cache = cache;
    }
    
    public RateLimit getRateLimit() {
        return rateLimit;
    }
    
    public void setRateLimit(RateLimit rateLimit) {
        this.rateLimit = rateLimit;
    }
    
    public static class Cache {
        private int ttl = 300;
        private int maxSize = 1000;
        
        public int getTtl() {
            return ttl;
        }
        
        public void setTtl(int ttl) {
            this.ttl = ttl;
        }
        
        public int getMaxSize() {
            return maxSize;
        }
        
        public void setMaxSize(int maxSize) {
            this.maxSize = maxSize;
        }
    }
    
    public static class RateLimit {
        private int requestsPerMinute = 100;
        
        public int getRequestsPerMinute() {
            return requestsPerMinute;
        }
        
        public void setRequestsPerMinute(int requestsPerMinute) {
            this.requestsPerMinute = requestsPerMinute;
        }
    }
}
