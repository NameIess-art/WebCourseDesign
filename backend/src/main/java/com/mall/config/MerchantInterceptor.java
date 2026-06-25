package com.mall.config;

import com.mall.context.MerchantContextHolder;
import com.mall.entity.UserAccount;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.servlet.HandlerInterceptor;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class MerchantInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof UserAccount user) {
            if (user.getRole() == com.mall.enums.UserRole.MERCHANT) {
                if (user.getMerchant() != null) {
                    MerchantContextHolder.setMerchantId(user.getMerchant().getId());
                }
            } else if (user.getRole() == com.mall.enums.UserRole.ADMIN) {
                String header = request.getHeader("X-Merchant-Id");
                if (header != null && !header.isEmpty() && !header.equals("null")) {
                    MerchantContextHolder.setMerchantId(Long.parseLong(header));
                }
            }
        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        MerchantContextHolder.clear();
    }
}
