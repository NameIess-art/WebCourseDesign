package com.mall.vo;

import java.time.LocalDateTime;

public record AfterSaleResponse(
        Long id,
        String orderNo,
        String type,
        String reason,
        String status,
        LocalDateTime createdAt
) {
}
