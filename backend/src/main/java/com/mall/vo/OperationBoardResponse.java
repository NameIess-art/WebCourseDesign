package com.mall.vo;

import java.util.List;

public record OperationBoardResponse(
        List<String> customerModules,
        List<String> merchantModules,
        List<String> platformModules,
        List<String> supportModules,
        List<String> highConcurrencyModules
) {
}
