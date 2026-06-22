package com.seatone.system.security;

import com.seatone.common.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;

/**
 * JWT 认证过滤器
 * 拦截请求并验证 Token 的有效性
 * 支持 Token 自动刷新机制
 */
@Slf4j
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    /**
     * Token 请求头名称
     */
    private static final String TOKEN_HEADER = "Authorization";

    /**
     * Token 前缀
     */
    private static final String TOKEN_PREFIX = "Bearer ";

    /**
     * 刷新后的 Token 响应头
     */
    private static final String REFRESH_TOKEN_HEADER = "X-Refresh-Token";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        // 获取请求中的 Token
        String token = getTokenFromRequest(request);

        // Token 存在且当前上下文未认证
        if (StringUtils.hasText(token) && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                // 验证 Token 有效性
                if (JwtUtils.validateToken(token)) {
                    // 解析 Token 获取用户信息
                    Claims claims = JwtUtils.parseToken(token);
                    Long userId = Long.valueOf(claims.get("userId").toString());
                    String username = claims.get("username").toString();

                    // 构建 UserDetails
                    UserDetails userDetails = new User(String.valueOf(userId), "", new ArrayList<>());

                    // 创建认证对象
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    // 设置安全上下文
                    SecurityContextHolder.getContext().setAuthentication(authentication);

                    // 检查 Token 是否需要刷新
                    if (JwtUtils.needRefresh(token)) {
                        String refreshedToken = JwtUtils.refreshToken(token);
                        response.setHeader(REFRESH_TOKEN_HEADER, refreshedToken);
                        log.debug("Token 已刷新，用户: {}", username);
                    }

                    log.debug("JWT 认证成功，用户: {}", username);
                }
            } catch (Exception e) {
                log.warn("JWT 认证失败: {}", e.getMessage());
                SecurityContextHolder.clearContext();
            }
        }

        filterChain.doFilter(request, response);
    }

    /**
     * 从请求中获取 Token
     *
     * @param request HTTP 请求
     * @return Token 字符串
     */
    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader(TOKEN_HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(TOKEN_PREFIX)) {
            return bearerToken.substring(TOKEN_PREFIX.length());
        }
        return null;
    }
}
