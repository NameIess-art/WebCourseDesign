package com.mall.repository;

import com.mall.entity.PointRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PointRecordRepository extends JpaRepository<PointRecord, Long> {

    List<PointRecord> findByUserIdOrderByCreatedAtDesc(Long userId);
}
