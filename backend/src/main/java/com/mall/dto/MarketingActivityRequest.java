package com.mall.dto;

import jakarta.validation.constraints.NotBlank;

public record MarketingActivityRequest(
        @NotBlank String title,
        @NotBlank String type,
        @NotBlank String ruleText
) {
}
