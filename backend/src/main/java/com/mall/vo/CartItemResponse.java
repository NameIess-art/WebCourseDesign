package com.mall.vo;

import java.math.BigDecimal;

public record CartItemResponse(
        Long id,
        Long productId,
        Long skuId,
        String skuName,
        String productName,
        String imageUrl,
        BigDecimal price,
        Integer stock,
        Integer quantity,
        BigDecimal subtotal
) {
}
