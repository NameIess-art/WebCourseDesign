package com.mall.repository;

import com.mall.entity.OrderAuditRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderAuditRecordRepository extends JpaRepository<OrderAuditRecord, Long> {
}
