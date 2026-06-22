package com.mall.repository;

import com.mall.entity.PromotionRule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PromotionRuleRepository extends JpaRepository<PromotionRule, Long> {

    List<PromotionRule> findByStatusOrderByCreatedAtDesc(String status);
}
