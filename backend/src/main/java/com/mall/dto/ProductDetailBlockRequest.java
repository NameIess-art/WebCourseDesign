package com.mall.dto;

import jakarta.validation.constraints.NotBlank;

public record ProductDetailBlockRequest(
        Long id,
        @NotBlank String blockType,
        @NotBlank String content,
        Integer sortOrder
) {
}
