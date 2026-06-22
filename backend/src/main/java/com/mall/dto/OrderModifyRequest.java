package com.mall.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;

public record OrderModifyRequest(
        @NotBlank String shippingAddress,
        @DecimalMin("0.01") BigDecimal totalAmount,
        @NotBlank String remark
) {
}
