package com.mall.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

// 注册接口的请求体：浏览器先做基础校验，后端仍用注解再次限制字段格式。
public record RegisterRequest(
        // 用户名用于登录，长度限制避免过短或异常长文本进入数据库。
        @NotBlank @Size(min = 3, max = 50) String username,
        // 密码只在请求中明文传输到后端，服务层会立即做哈希加密后再保存。
        @NotBlank @Size(min = 6, max = 100) String password,
        // 邮箱既用于账号唯一性校验，也用于忘记密码流程。
        @NotBlank @Email String email,
        // 展示名用于页面显示，注册页默认与用户名保持一致。
        @NotBlank @Size(min = 2, max = 100) String displayName
) {
}
