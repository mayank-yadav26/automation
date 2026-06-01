package com.automation.model;

import lombok.Data;

import java.util.List;

@Data
public class SectorOutlook {
    private List<String> resilientSectors;
    private List<String> underPressureSectors;
}
