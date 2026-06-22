package com.mall.vo;

import java.time.LocalDateTime;

public record ProductReviewResponse(
        Long id,
        String username,
        Integer rating,
        String content,
        LocalDateTime createdAt
) {
}
