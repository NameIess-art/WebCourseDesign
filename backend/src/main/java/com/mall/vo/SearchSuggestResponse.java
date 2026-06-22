package com.mall.vo;

import java.util.List;

public record SearchSuggestResponse(
        List<String> productNames,
        List<String> categories
) {
}
