package com.automation.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class StockRecommendation {
    
    @JsonProperty("stock_name")
    private String stockName;
    
    @JsonProperty("ticker")
    private String ticker;
    
    @JsonProperty("entry_range")
    private String entryRange;
    
    @JsonProperty("stop_loss")
    private String stopLoss;
    
    @JsonProperty("target_price")
    private String targetPrice;
    
    @JsonProperty("max_exit_days")
    private Integer maxExitDays;
    
    @JsonProperty("reason_to_buy")
    private String reasonToBuy;
    
    @Override
    public String toString() {
        return "StockRecommendation{" +
                "stockName='" + stockName + '\'' +
                ", ticker='" + ticker + '\'' +
                ", entryRange='" + entryRange + '\'' +
                ", stopLoss='" + stopLoss + '\'' +
                ", targetPrice='" + targetPrice + '\'' +
                ", maxExitDays=" + maxExitDays +
                ", reasonToBuy='" + reasonToBuy + '\'' +
                '}';
    }
}
