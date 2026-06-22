package com.mall.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "order_modify_records")
public class OrderModifyRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private OrderEntity order;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal beforeAmount;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal afterAmount;

    @Column(nullable = false, length = 255)
    private String beforeAddress;

    @Column(nullable = false, length = 255)
    private String afterAddress;

    @Column(nullable = false, length = 500)
    private String remark;

    @Column(nullable = false)
    private LocalDateTime createdAt;
}
