package com.mall.repository;

import com.mall.entity.AfterSaleRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AfterSaleRecordRepository extends JpaRepository<AfterSaleRecord, Long> {

    List<AfterSaleRecord> findByUserIdOrderByCreatedAtDesc(Long userId);
}
