package com.mall.vo;

import java.math.BigDecimal;

public record ReportResponse(
        Long visitorCount,
        Long conversionCount,
        BigDecimal revenue,
        Integer stockWarningCount,
        String userPortrait,
        String marketingEffect
) {
}
