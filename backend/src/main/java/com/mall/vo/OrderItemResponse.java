package com.mall.vo;

import java.math.BigDecimal;

public record OrderItemResponse(
        Long id,
        Long productId,
        Long skuId,
        String skuName,
        String productName,
        Integer quantity,
        BigDecimal price
) {
}
