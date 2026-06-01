package com.automation.repository;

import com.automation.document.AnalysisRecord;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AnalysisRecordRepository extends MongoRepository<AnalysisRecord, String> {
}
