package com.mall.service;

import com.mall.dto.ForgotPasswordRequest;
import com.mall.dto.LoginRequest;
import com.mall.dto.RegisterRequest;
import com.mall.dto.UpdateAccountRequest;
import com.mall.entity.UserAccount;
import com.mall.enums.UserRole;
import com.mall.exception.BusinessException;
import com.mall.mapper.UserMapper;
import com.mall.security.JwtService;
import com.mall.vo.AuthResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Random;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    /**
     * 用户注册
     * 处理用户的注册请求，校验用户名和邮箱的唯一性，并将用户信息存入数据库
     * @param request 包含用户名、密码、邮箱、展示名称等信息的注册请求对象
     */
    @Transactional
    public void register(RegisterRequest request) {
        // 校验用户名是否已被注册
        if (userMapper.existsByUsername(request.username())) {
            throw new BusinessException("用户名已存在");
        }
        // 校验邮箱是否已被绑定
        if (userMapper.existsByEmail(request.email())) {
            throw new BusinessException("邮箱已被使用");
        }

        // 创建新用户实体对象
        UserAccount user = new UserAccount();
        user.setUsername(request.username());
        // 密码加密存储
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setEmail(request.email());
        user.setDisplayName(request.displayName());
        // 默认赋予普通用户角色
        user.setRole(UserRole.USER);
        
        // 将新用户持久化保存至数据库
        userMapper.save(user);
    }

    /**
     * 用户登录
     * 校验用户的凭证并生成访问凭证 (JWT Token) 返回给前端
     * @param request 包含用户名和密码的登录请求对象
     * @return 认证响应，包含生成的 Token 以及用户的基本信息
     */
    public AuthResponse login(LoginRequest request) {
        // 根据用户名在数据库中查找用户，如果找不到则抛出异常
        UserAccount user = userMapper.findByUsername(request.username())
                .orElseThrow(() -> new BusinessException("Invalid username or password"));

        // 校验用户输入的密码和数据库中的加密密码是否匹配
        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new BusinessException("Invalid username or password");
        }

        // 认证成功，生成包含用户信息的 JWT Token
        String token = jwtService.generateToken(user.getId(), user.getUsername(), user.getRole().name());
        
        // 封装响应对象并返回给前端
        return new AuthResponse(token, user.getId(), user.getUsername(), user.getDisplayName(), user.getRole().name());
    }

    @Transactional
    public void updateAccount(Long userId, UpdateAccountRequest request) {
        UserAccount user = userMapper.findById(userId)
                .orElseThrow(() -> new BusinessException("用户不存在"));

        if (!passwordEncoder.matches(request.oldPassword(), user.getPassword())) {
            throw new BusinessException("原密码不正确");
        }

        if (!user.getUsername().equals(request.username())) {
            if (userMapper.existsByUsername(request.username())) {
                throw new BusinessException("新用户名已存在");
            }
            user.setUsername(request.username());
            user.setDisplayName(request.username());
        }

        user.setPassword(passwordEncoder.encode(request.newPassword()));
        userMapper.save(user);
    }

    @Transactional
    public String forgotPassword(ForgotPasswordRequest request) {
        UserAccount user = userMapper.findByEmail(request.email())
                .orElseThrow(() -> new BusinessException("未找到该邮箱对应的账号"));

        // Generate a random 6-digit password
        String newPassword = String.format("%06d", new Random().nextInt(1000000));
        user.setPassword(passwordEncoder.encode(newPassword));
        userMapper.save(user);

        return newPassword;
    }
}
