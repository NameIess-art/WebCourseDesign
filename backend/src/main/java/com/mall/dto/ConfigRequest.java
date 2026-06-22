package com.mall.dto;

import jakarta.validation.constraints.NotBlank;

public record ConfigRequest(
        @NotBlank String value,
        @NotBlank String description
) {
}
