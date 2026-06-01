package com.automation.model;

import lombok.Data;

@Data
public class TechnicalIndicators {
    private Double rsi;
    private String macd;
    private Double movingAverage50;
    private Double movingAverage200;
}
