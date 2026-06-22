package com.mall.vo;

import java.time.LocalDateTime;

public record PointRecordResponse(
        Long id,
        Integer points,
        String type,
        String description,
        LocalDateTime createdAt
) {
}
