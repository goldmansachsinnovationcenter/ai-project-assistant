package com.example.springai.service.search.impl;

import com.example.springai.model.search.SearchParameters;
import com.example.springai.model.search.SearchResult;
import com.example.springai.model.search.SearchQuery.QueryIntent;
import com.example.springai.service.search.SearchService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Placeholder implementation for market data search
 * Handles queries like "David kostin's view on the s&p 500"
 */
@Service
public class MarketDataSearchServiceImpl implements SearchService {

    @Override
    public List<SearchResult> search(SearchParameters parameters) {
        List<SearchResult> results = new ArrayList<>();
        
        boolean isSP500Query = parameters.getEntities() != null && 
                parameters.getEntities().stream()
                    .anyMatch(e -> e.toLowerCase().contains("s&p") || 
                             e.toLowerCase().contains("s&p 500") || 
                             e.toLowerCase().contains("sp500"));
        
        boolean isAnalystQuery = parameters.getSearchTerm() != null && 
                parameters.getSearchTerm().toLowerCase().contains("kostin") ||
                (parameters.getEntities() != null && 
                 parameters.getEntities().stream()
                    .anyMatch(e -> e.toLowerCase().contains("kostin") || 
                             e.toLowerCase().contains("goldman") || 
                             e.toLowerCase().contains("analyst")));
        
        if (isSP500Query) {
            results.add(createMarketDataResult(
                "S&P 500 Market Analysis - Q1 2025",
                "The S&P 500 has shown resilience in Q1 2025, with a 7.2% gain despite economic headwinds. Technology and healthcare sectors led the advance.",
                "Market Data API",
                "index_analysis",
                0.95,
                LocalDateTime.now().minusDays(5),
                Arrays.asList("S&P 500", "index", "market performance"),
                Arrays.asList("market analysis", "index performance", "sector rotation"),
                "https://api.example.com/market-data/sp500/q1-2025"
            ));
            
            results.add(createMarketDataResult(
                "S&P 500 Technical Indicators - March 2025",
                "Technical analysis shows the S&P 500 approaching resistance at 5,800. RSI indicates overbought conditions at 72, suggesting potential consolidation.",
                "Market Data API",
                "technical_analysis",
                0.85,
                LocalDateTime.now().minusDays(2),
                Arrays.asList("S&P 500", "technical analysis", "RSI"),
                Arrays.asList("resistance", "overbought", "consolidation"),
                "https://api.example.com/market-data/sp500/technical/march-2025"
            ));
        }
        
        if (isAnalystQuery) {
            results.add(createMarketDataResult(
                "David Kostin's S&P 500 Outlook - 2025",
                "Goldman Sachs Chief US Equity Strategist David Kostin maintains a year-end S&P 500 target of 6,200, representing a 12% upside from current levels. Kostin cites strong corporate earnings growth and continued economic expansion as key drivers.",
                "Analyst Reports API",
                "analyst_report",
                0.98,
                LocalDateTime.now().minusDays(10),
                Arrays.asList("David Kostin", "Goldman Sachs", "S&P 500", "outlook"),
                Arrays.asList("price target", "equity strategy", "earnings growth"),
                "https://api.example.com/analyst-reports/goldman/kostin/sp500-outlook-2025"
            ));
            
            results.add(createMarketDataResult(
                "Goldman Sachs US Equity Strategy - Sector Allocation",
                "David Kostin's team recommends overweight positions in Technology and Healthcare, neutral on Financials and Consumer Discretionary, and underweight on Utilities and Real Estate for the remainder of 2025.",
                "Analyst Reports API",
                "sector_allocation",
                0.90,
                LocalDateTime.now().minusDays(15),
                Arrays.asList("David Kostin", "Goldman Sachs", "sector allocation"),
                Arrays.asList("overweight", "underweight", "portfolio strategy"),
                "https://api.example.com/analyst-reports/goldman/kostin/sector-allocation-2025"
            ));
        }
        
        if (results.isEmpty()) {
            results.add(createMarketDataResult(
                "Latest Market Trends - March 2025",
                "Market overview showing current trends across major indices, sectors, and asset classes. Includes volatility metrics and correlation analysis.",
                "Market Data API",
                "market_overview",
                0.75,
                LocalDateTime.now().minusDays(1),
                Arrays.asList("market trends", "indices", "sectors"),
                Arrays.asList("overview", "volatility", "correlation"),
                "https://api.example.com/market-data/trends/march-2025"
            ));
        }
        
        return results;
    }

    private SearchResult createMarketDataResult(
            String title, 
            String content, 
            String source, 
            String sourceType, 
            double relevanceScore, 
            LocalDateTime publishDate, 
            List<String> entities, 
            List<String> keywords, 
            String url) {
        
        SearchResult result = new SearchResult();
        result.setId(String.valueOf(System.nanoTime()));
        result.setTitle(title);
        result.setContent(content);
        result.setSource(source);
        result.setSourceType(sourceType);
        result.setRelevanceScore(relevanceScore);
        result.setPublishDate(publishDate);
        result.setEntities(entities);
        result.setKeywords(keywords);
        result.setUrl(url);
        
        return result;
    }

    @Override
    public String getServiceName() {
        return "Market Data Search Service";
    }

    @Override
    public boolean isApplicable(SearchParameters parameters) {
        if (parameters.getSearchTerm() != null) {
            String query = parameters.getSearchTerm().toLowerCase();
            
            boolean hasMarketTerms = query.contains("market") || 
                                    query.contains("index") || 
                                    query.contains("s&p") || 
                                    query.contains("dow") || 
                                    query.contains("nasdaq") || 
                                    query.contains("russell") ||
                                    query.contains("view on");
            
            boolean hasAnalystNames = query.contains("kostin") || 
                                     query.contains("analyst") || 
                                     query.contains("strategist");
            
            return hasMarketTerms || hasAnalystNames;
        }
        
        return false;
    }
}
