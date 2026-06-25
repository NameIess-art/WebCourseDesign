package com.mall.controller;

import com.mall.dto.ForgotPasswordRequest;
import com.mall.dto.LoginRequest;
import com.mall.dto.RegisterRequest;
import com.mall.dto.UpdateAccountRequest;
import com.mall.service.AuthService;
import com.mall.vo.ApiResponse;
import com.mall.vo.AuthResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ApiResponse<Void> register(@Valid @RequestBody RegisterRequest request) {
        // 请求体先转换为注册请求对象，并通过注解校验字段格式。
        authService.register(request);
        return ApiResponse.success();
    }

    @PostMapping("/login")
    public ApiResponse<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        // 登录成功后返回统一响应，前端会保存其中的登录令牌和用户角色。
        return ApiResponse.success(authService.login(request));
    }

    @PutMapping("/account")
    public ApiResponse<Void> updateAccount(@Valid @RequestBody UpdateAccountRequest request) {
        // 当前用户编号来自安全上下文，避免前端伪造要修改的账号编号。
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = Long.valueOf((String) authentication.getPrincipal());
        authService.updateAccount(userId, request);
        return ApiResponse.success();
    }

    @PostMapping("/forgot-password")
    public ApiResponse<String> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        // 根据邮箱重置密码，演示项目直接把新密码放在响应中返回给前端。
        return ApiResponse.success(authService.forgotPassword(request));
    }
}
