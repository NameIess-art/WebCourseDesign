package com.mall.vo;

public record SimpleItemResponse(
        Long id,
        String title,
        String type,
        String content,
        String status
) {
}
