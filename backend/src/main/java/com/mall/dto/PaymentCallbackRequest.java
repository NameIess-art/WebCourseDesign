package com.mall.dto;

import jakarta.validation.constraints.NotBlank;

public record PaymentCallbackRequest(
        @NotBlank String paymentNo,
        @NotBlank String channelTradeNo
) {
}
