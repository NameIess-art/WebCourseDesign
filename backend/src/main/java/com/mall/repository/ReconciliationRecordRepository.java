package com.mall.repository;

import com.mall.entity.ReconciliationRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface ReconciliationRecordRepository extends JpaRepository<ReconciliationRecord, Long> {

    Optional<ReconciliationRecord> findByBizDate(LocalDate bizDate);
}
