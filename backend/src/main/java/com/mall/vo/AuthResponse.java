package com.mall.vo;

public record AuthResponse(
        String token,
        Long userId,
        String username,
        String displayName,
        String role
) {
}
