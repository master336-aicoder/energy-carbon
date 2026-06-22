package com.seatone.common.utils;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * 安全工具类
 * 提供获取当前登录用户信息的方法，基于 Spring Security 上下文
 *
 * @author seatone
 * @since 2026-06-22
 */
public class SecurityUtils {

    /**
     * 获取当前登录用户ID
     *
     * @return 当前用户ID，未登录返回 null
     */
    public static Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() != null) {
            // 根据实际UserDetails实现返回用户ID
            return Long.valueOf(authentication.getName());
        }
        return null;
    }

    /**
     * 获取当前登录用户名
     *
     * @return 当前用户名，未登录返回 null
     */
    public static String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            return authentication.getName();
        }
        return null;
    }
}
