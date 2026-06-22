package com.mall.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AddressRequest(
        @NotBlank String receiver,
        @NotBlank String phone,
        @NotBlank String region,
        @NotBlank String detail,
        @NotNull Boolean defaultAddress
) {
}
