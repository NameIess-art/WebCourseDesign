package com.mall.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record SeckillEventRequest(
        @NotNull Long productId,
        @NotNull @DecimalMin("0.01") BigDecimal seckillPrice,
        @NotNull @Min(1) Integer stock
) {
}
