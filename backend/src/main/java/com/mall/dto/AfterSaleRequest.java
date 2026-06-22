package com.mall.dto;

import jakarta.validation.constraints.NotBlank;

public record AfterSaleRequest(
        @NotBlank String type,
        @NotBlank String reason
) {
}
