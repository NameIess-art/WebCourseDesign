package com.mall.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PaymentRequest(
        @NotNull Long orderId,
        @NotBlank String channel,
        String idempotencyKey
) {
}
