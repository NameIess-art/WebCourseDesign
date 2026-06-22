package com.mall.vo;

import java.util.List;

public record RecommendationResponse(
        List<ProductResponse> hot,
        List<ProductResponse> latest,
        List<ProductResponse> activity
) {
}
