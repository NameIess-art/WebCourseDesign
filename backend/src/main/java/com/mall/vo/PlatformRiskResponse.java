package com.mall.vo;

import java.time.LocalDateTime;

public record PlatformRiskResponse(
        Long id,
        String target,
        String riskType,
        String description,
        String status,
        LocalDateTime createdAt
) {
}
