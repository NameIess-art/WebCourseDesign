package com.mall.repository;

import com.mall.entity.AfterSaleRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface AfterSaleRecordRepository extends JpaRepository<AfterSaleRecord, Long> {

    List<AfterSaleRecord> findByUserIdOrderByCreatedAtDesc(Long userId);
    Page<AfterSaleRecord> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);
}
