package com.mall.vo;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PaymentResponse(
        Long id,
        String paymentNo,
        Long orderId,
        String channel,
        BigDecimal amount,
        String status,
        LocalDateTime createdAt,
        LocalDateTime paidAt
) {
}
