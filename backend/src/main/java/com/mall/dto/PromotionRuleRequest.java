package com.mall.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;

public record PromotionRuleRequest(
        @NotBlank String title,
        @NotBlank String promotionType,
        @DecimalMin("0.00") BigDecimal thresholdAmount,
        @DecimalMin("0.00") BigDecimal discountAmount,
        @NotBlank String ruleText
) {
}
