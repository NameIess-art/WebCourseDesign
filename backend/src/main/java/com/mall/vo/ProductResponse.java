package com.mall.vo;

import java.math.BigDecimal;
import java.util.List;

public record ProductResponse(
        Long id,
        String name,
        String subtitle,
        String description,
        String imageUrl,
        BigDecimal price,
        Integer stock,
        Integer sales,
        Boolean active,
        String skuCode,
        String spec,
        String promotionTag,
        Integer favoriteCount,
        Integer questionCount,
        BigDecimal rating,
        List<ProductSkuResponse> skus,
        List<ProductDetailBlockResponse> detailBlocks,
        Long categoryId,
        String categoryName
) {
}
