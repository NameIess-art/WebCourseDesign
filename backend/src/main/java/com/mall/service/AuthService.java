package com.mall.service;

import com.mall.dto.ForgotPasswordRequest;
import com.mall.dto.LoginRequest;
import com.mall.dto.RegisterRequest;
import com.mall.dto.UpdateAccountRequest;
import com.mall.entity.UserAccount;
import com.mall.enums.UserRole;
import com.mall.exception.BusinessException;
import com.mall.repository.UserRepository;
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

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Transactional
    public void register(RegisterRequest request) {
        // 用户名和邮箱属于业务唯一性校验，必须查询数据库才能判断。
        if (userRepository.existsByUsername(request.username())) {
            throw new BusinessException("用户名已存在");
        }
        if (userRepository.existsByEmail(request.email())) {
            throw new BusinessException("邮箱已被使用");
        }

        // 入库前对密码做哈希加密，数据库中不保存用户明文密码。
        UserAccount user = new UserAccount();
        user.setUsername(request.username());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setEmail(request.email());
        user.setDisplayName(request.displayName());
        user.setRole(UserRole.USER);
        userRepository.save(user);
    }

    public AuthResponse login(LoginRequest request) {
        // 先按用户名定位账号，账号不存在和密码错误都返回同一种提示，避免暴露账号状态。
        UserAccount user = userRepository.findByUsername(request.username())
                .orElseThrow(() -> new BusinessException("Invalid username or password"));

        // 用密码编码器比对明文密码和数据库中的哈希值。
        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new BusinessException("Invalid username or password");
        }

        // 登录成功后签发令牌，前端后续请求会放入认证请求头。
        String token = jwtService.generateToken(user.getId(), user.getUsername(), user.getRole().name());
        return new AuthResponse(token, user.getId(), user.getUsername(), user.getDisplayName(), user.getRole().name());
    }

    @Transactional
    public void updateAccount(Long userId, UpdateAccountRequest request) {
        UserAccount user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("用户不存在"));

        if (!passwordEncoder.matches(request.oldPassword(), user.getPassword())) {
            throw new BusinessException("原密码不正确");
        }

        if (!user.getUsername().equals(request.username())) {
            if (userRepository.existsByUsername(request.username())) {
                throw new BusinessException("新用户名已存在");
            }
            user.setUsername(request.username());
            user.setDisplayName(request.username());
        }

        user.setPassword(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);
    }

    @Transactional
    public String forgotPassword(ForgotPasswordRequest request) {
        UserAccount user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new BusinessException("未找到该邮箱对应的账号"));

        // 生成 6 位随机新密码，并立即加密写回数据库。
        String newPassword = String.format("%06d", new Random().nextInt(1000000));
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        return newPassword;
    }
}
