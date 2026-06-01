package com.automation.model;

import lombok.Data;

import java.util.List;

@Data
public class StockRecommendation {
    private String companyName;
    private String tickerSymbol;
    private String sector;
    private Double currentStockPriceNSE;
    private EntryRange entryRange;
    private Double stopLoss;
    private Double targetPrice;
    private Integer maxHoldingDays;
    private String reasonToBuy;
    private String riskLevel;
    private Double confidenceScore;
    private List<Double> supportLevels;
    private List<Double> resistanceLevels;
    private TechnicalIndicators technicalIndicators;
}
