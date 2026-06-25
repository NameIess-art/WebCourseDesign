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

    @Transactional
    public void register(RegisterRequest request) {
        if (userMapper.existsByUsername(request.username())) {
            throw new BusinessException("用户名已存在");
        }
        if (userMapper.existsByEmail(request.email())) {
            throw new BusinessException("邮箱已被使用");
        }

        UserAccount user = new UserAccount();
        user.setUsername(request.username());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setEmail(request.email());
        user.setDisplayName(request.displayName());
        user.setRole(UserRole.USER);
        userMapper.save(user);
    }

    public AuthResponse login(LoginRequest request) {
        UserAccount user = userMapper.findByUsername(request.username())
                .orElseThrow(() -> new BusinessException("Invalid username or password"));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new BusinessException("Invalid username or password");
        }

        String token = jwtService.generateToken(user.getId(), user.getUsername(), user.getRole().name());
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
