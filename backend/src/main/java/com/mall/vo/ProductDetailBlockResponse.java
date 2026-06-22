package com.mall.vo;

public record ProductDetailBlockResponse(
        Long id,
        String blockType,
        String content,
        Integer sortOrder
) {
}
