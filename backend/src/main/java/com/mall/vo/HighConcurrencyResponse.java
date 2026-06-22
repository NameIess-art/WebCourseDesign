package com.mall.vo;

import java.util.List;

public record HighConcurrencyResponse(
        String stockDeductStrategy,
        String rateLimitStrategy,
        String cacheStrategy,
        String mqStrategy,
        String transactionStrategy,
        List<String> stressChecklist
) {
}
