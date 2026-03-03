package com.automation.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class IndiaSectorOutlook {
    
    @JsonProperty("resilient_sectors")
    private List<String> resilientSectors;
    
    @JsonProperty("under_pressure")
    private List<String> underPressure;
    
    @Override
    public String toString() {
        return "IndiaSectorOutlook{" +
                "resilientSectors=" + resilientSectors +
                ", underPressure=" + underPressure +
                '}';
    }
}
