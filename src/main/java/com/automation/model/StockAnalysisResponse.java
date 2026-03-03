package com.automation.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class StockAnalysisResponse {
    
    @JsonProperty("market_status")
    private MarketStatus marketStatus;
    
    @JsonProperty("swing_trading_recommendations")
    private List<StockRecommendation> swingTradingRecommendations;
    
    @JsonProperty("disclaimer")
    private String disclaimer;
    
    @Override
    public String toString() {
        return "StockAnalysisResponse{" +
                "marketStatus=" + marketStatus +
                ", swingTradingRecommendations=" + swingTradingRecommendations +
                ", disclaimer='" + disclaimer + '\'' +
                '}';
    }
}
