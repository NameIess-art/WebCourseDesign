package com.mall.entity;

import com.mall.enums.OrderStatus;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "orders")
public class OrderEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 40)
    private String orderNo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserAccount user;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;

    @Column(precision = 10, scale = 2)
    private BigDecimal originalAmount = BigDecimal.ZERO;

    @Column(precision = 10, scale = 2)
    private BigDecimal discountAmount = BigDecimal.ZERO;

    @Column
    private Integer pointsUsed = 0;

    @Column(precision = 10, scale = 2)
    private BigDecimal pointsDiscountAmount = BigDecimal.ZERO;

    @Column(nullable = false, length = 255)
    private String shippingAddress;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private OrderStatus status;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime paidAt;

    @Column(length = 40)
    private String paymentChannel;

    @Column(length = 30)
    private String auditStatus = "PENDING";

    @Column(length = 500)
    private String merchantRemark;

    @Column(length = 80)
    private String idempotencyKey;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<OrderItem> items = new ArrayList<>();
}
