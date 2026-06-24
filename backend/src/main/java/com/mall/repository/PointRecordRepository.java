package com.mall.repository;

import com.mall.entity.PointRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PointRecordRepository extends JpaRepository<PointRecord, Long> {

    List<PointRecord> findByUserIdOrderByCreatedAtDesc(Long userId);
    Page<PointRecord> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);
}
