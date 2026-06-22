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
import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "reconciliation_records")
public class ReconciliationRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate bizDate;

    @Column(nullable = false)
    private Long orderCount;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal orderAmount;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal paymentAmount;

    @Column(nullable = false, length = 30)
    private String status;
}
