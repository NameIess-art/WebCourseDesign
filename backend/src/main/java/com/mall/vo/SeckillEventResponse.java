package com.mall.vo;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record SeckillEventResponse(
        Long id,
        Long productId,
        String productName,
        String imageUrl,
        BigDecimal seckillPrice,
        Integer stock,
        Integer sold,
        Boolean active,
        LocalDateTime startAt,
        LocalDateTime endAt
) {
}
