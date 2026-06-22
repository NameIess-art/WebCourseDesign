package com.mall.vo;

import java.util.List;

public record LogisticsResponse(
        Long orderId,
        String orderNo,
        String carrier,
        String trackingNo,
        List<String> traces
) {
}
