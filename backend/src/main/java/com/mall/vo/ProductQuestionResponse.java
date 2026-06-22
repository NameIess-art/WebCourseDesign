package com.mall.vo;

import java.time.LocalDateTime;

public record ProductQuestionResponse(
        Long id,
        String username,
        String question,
        String answer,
        LocalDateTime createdAt
) {
}
