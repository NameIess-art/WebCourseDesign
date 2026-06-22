package com.mall.dto;

import jakarta.validation.constraints.NotBlank;

public record AfterSaleProcessRequest(
        @NotBlank String status,
        @NotBlank String remark
) {
}
