package com.mall.vo;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record FavoriteResponse(
        Long id,
        Long productId,
        String productName,
        String imageUrl,
        BigDecimal price,
        LocalDateTime createdAt
) {
}
