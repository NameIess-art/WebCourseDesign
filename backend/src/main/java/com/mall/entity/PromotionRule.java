package com.mall.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "promotion_rules")
public class PromotionRule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 80)
    private String title;

    @Column(nullable = false, length = 30)
    private String promotionType;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal thresholdAmount = BigDecimal.ZERO;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal discountAmount = BigDecimal.ZERO;

    @Column(nullable = false, length = 500)
    private String ruleText;

    @Column(nullable = false, length = 30)
    private String status = "ACTIVE";

    @Column(nullable = false)
    private LocalDateTime createdAt;
}
