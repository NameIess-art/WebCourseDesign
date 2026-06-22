package com.mall.vo;

public record SystemConfigResponse(
        Long id,
        String key,
        String value,
        String description
) {
}
