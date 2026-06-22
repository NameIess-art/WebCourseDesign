package com.mall.vo;

import java.math.BigDecimal;

public record ProductSkuResponse(
        Long id,
        String skuCode,
        String specName,
        BigDecimal price,
        Integer stock,
        Boolean active
) {
}
