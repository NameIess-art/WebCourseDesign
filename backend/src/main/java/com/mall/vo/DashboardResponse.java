package com.mall.vo;

import java.math.BigDecimal;

public record DashboardResponse(
        long userCount,
        long productCount,
        long orderCount,
        long pendingOrderCount,
        BigDecimal totalRevenue
) {
}
