package com.mall.repository;

import com.mall.entity.MarketingActivity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MarketingActivityRepository extends JpaRepository<MarketingActivity, Long> {

    List<MarketingActivity> findAllByOrderByStartAtDesc();
    Page<MarketingActivity> findAllByOrderByStartAtDesc(Pageable pageable);

    List<MarketingActivity> findByStatusIgnoreCaseOrderByStartAtDesc(String status);
    Page<MarketingActivity> findByStatusIgnoreCaseOrderByStartAtDesc(String status, Pageable pageable);
}
