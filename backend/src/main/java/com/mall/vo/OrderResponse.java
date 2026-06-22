package com.mall.vo;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderResponse(
        Long id,
        String orderNo,
        String status,
        BigDecimal totalAmount,
        BigDecimal originalAmount,
        BigDecimal discountAmount,
        Integer pointsUsed,
        BigDecimal pointsDiscountAmount,
        String paymentChannel,
        String auditStatus,
        String shippingAddress,
        LocalDateTime createdAt,
        List<OrderItemResponse> items
) {
}
