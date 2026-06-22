package com.mall.repository;

import com.mall.entity.IdempotentRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IdempotentRecordRepository extends JpaRepository<IdempotentRecord, Long> {

    boolean existsByIdemKey(String idemKey);
}
