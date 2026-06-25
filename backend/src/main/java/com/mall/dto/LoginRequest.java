package com.mall.dto;

import jakarta.validation.constraints.NotBlank;

// 登录接口的请求体：字段来自前端登录表单。
public record LoginRequest(
        // 后端会根据用户名查询用户，再比对密码哈希。
        @NotBlank String username,
        // 非空校验保证空密码请求不会进入服务层密码比对。
        @NotBlank String password
) {
}
