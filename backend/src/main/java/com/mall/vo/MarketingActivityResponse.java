package com.mall.vo;

import java.time.LocalDateTime;

public record MarketingActivityResponse(
        Long id,
        String title,
        String type,
        String ruleText,
        String status,
        LocalDateTime startAt,
        LocalDateTime endAt
) {
}
