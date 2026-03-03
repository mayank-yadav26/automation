package com.automation.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class MarketStatus {
    
    @JsonProperty("global_sentiment")
    private String globalSentiment;
    
    @JsonProperty("india_sector_outlook")
    private IndiaSectorOutlook indiaSectorOutlook;
    
    @Override
    public String toString() {
        return "MarketStatus{" +
                "globalSentiment='" + globalSentiment + '\'' +
                ", indiaSectorOutlook=" + indiaSectorOutlook +
                '}';
    }
}
