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
                // 商家账号只能操作自己绑定的商家数据。
                if (user.getMerchant() != null) {
                    MerchantContextHolder.setMerchantId(user.getMerchant().getId());
                }
            } else if (user.getRole() == com.mall.enums.UserRole.ADMIN) {
                // 管理员通过前端传来的商家编号切换当前管理对象。
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
        // 请求结束后清理线程变量，避免下一次请求复用到旧商家编号。
        MerchantContextHolder.clear();
    }
}
