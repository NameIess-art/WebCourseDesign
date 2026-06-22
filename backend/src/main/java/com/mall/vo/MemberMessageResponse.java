package com.mall.vo;

import java.time.LocalDateTime;

public record MemberMessageResponse(
        Long id,
        String title,
        String content,
        Boolean readFlag,
        LocalDateTime createdAt
) {
}
