package com.mall.dto;

import jakarta.validation.constraints.NotBlank;

public record AdminTextRequest(
        @NotBlank String title,
        @NotBlank String type,
        @NotBlank String content
) {
}
