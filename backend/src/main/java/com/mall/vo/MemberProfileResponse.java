package com.mall.vo;

public record MemberProfileResponse(
        Long userId,
        String username,
        String displayName,
        String level,
        Integer points,
        String rights,
        Long unreadMessages
) {
}
