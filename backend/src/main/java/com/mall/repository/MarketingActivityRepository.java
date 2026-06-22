package com.mall.repository;

import com.mall.entity.MarketingActivity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MarketingActivityRepository extends JpaRepository<MarketingActivity, Long> {

    List<MarketingActivity> findAllByOrderByStartAtDesc();
}
