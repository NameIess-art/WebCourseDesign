package com.mall.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record OrderAuditRequest(
        @NotNull Boolean approved,
        @NotBlank String remark
) {
}
