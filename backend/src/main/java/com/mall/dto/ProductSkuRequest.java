package com.mall.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record ProductSkuRequest(
        Long id,
        @NotBlank String skuCode,
        @NotBlank String specName,
        @NotNull @DecimalMin("0.01") BigDecimal price,
        @NotNull @Min(0) Integer stock,
        Boolean active
) {
}
