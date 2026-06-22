package com.mall.vo;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CouponResponse(
        Long id,
        String name,
        BigDecimal thresholdAmount,
        BigDecimal discountAmount,
        Integer stock,
        Boolean claimed,
        Boolean used,
        LocalDateTime validUntil
) {
}
