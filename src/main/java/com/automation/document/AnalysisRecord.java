package com.automation.document;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "analysis_records")
@Data
public class AnalysisRecord {

    @Id
    private String id;
    private String promptText;
    private String responseText;
    private LocalDateTime createdAt;

    public AnalysisRecord() {}

    public AnalysisRecord(String promptText, String responseText) {
        this.promptText = promptText;
        this.responseText = responseText;
        this.createdAt = LocalDateTime.now();
    }
}
