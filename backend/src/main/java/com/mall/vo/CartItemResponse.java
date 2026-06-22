package com.mall.vo;

import java.math.BigDecimal;

public record CartItemResponse(
        Long id,
        Long productId,
        String productName,
        String imageUrl,
        BigDecimal price,
        Integer quantity,
        BigDecimal subtotal
) {
}
