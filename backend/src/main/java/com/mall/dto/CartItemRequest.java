package com.mall.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record CartItemRequest(
        @NotNull Long productId,
        Long skuId,
        @NotNull @Min(1) Integer quantity
) {
}
