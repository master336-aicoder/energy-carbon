package com.seatone.common.aspect;

import com.seatone.common.annotation.RateLimit;
import com.seatone.common.exception.RateLimitException;
import com.seatone.common.utils.RedisCache;
import com.seatone.common.utils.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.TimeUnit;

/**
 * 接口限流切面
 * 基于Redis实现滑动窗口限流
 */
@Slf4j
@Aspect
@Component
public class RateLimitAspect {

    @Autowired
    private RedisCache redisCache;

    @Around("@annotation(com.seatone.common.annotation.RateLimit)")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        RateLimit rateLimit = signature.getMethod().getAnnotation(RateLimit.class);

        // 构建限流Key
        String limitKey = buildLimitKey(rateLimit, joinPoint);

        // 获取当前请求次数
        Long currentCount = redisCache.getCacheObject(limitKey);

        if (currentCount == null) {
            // 首次请求，设置计数器
            redisCache.setCacheObject(limitKey, 1L, (int) rateLimit.time(), rateLimit.timeUnit());
        } else if (currentCount >= rateLimit.count()) {
            // 超过限流阈值
            log.warn("接口限流触发: key={}, count={}, limit={}", limitKey, currentCount, rateLimit.count());
            throw new RateLimitException(rateLimit.message());
        } else {
            // 未超过限流阈值，计数器+1
            redisCache.incr(limitKey, 1);
        }

        return joinPoint.proceed();
    }

    /**
     * 构建限流Key
     */
    private String buildLimitKey(RateLimit rateLimit, ProceedingJoinPoint joinPoint) {
        StringBuilder keyBuilder = new StringBuilder("rate_limit:");

        // 添加限流维度
        switch (rateLimit.limitType()) {
            case IP:
                ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
                if (attributes != null) {
                    HttpServletRequest request = attributes.getRequest();
                    String ip = getClientIp(request);
                    keyBuilder.append("ip:").append(ip);
                } else {
                    keyBuilder.append("ip:unknown");
                }
                break;
            case USER:
                Long userId = SecurityUtils.getCurrentUserId();
                keyBuilder.append("user:").append(userId != null ? userId : "anonymous");
                break;
            case GLOBAL:
                keyBuilder.append("global");
                break;
        }

        // 添加接口标识
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        keyBuilder.append(":").append(className).append(":").append(methodName);

        return keyBuilder.toString();
    }

    /**
     * 获取客户端真实IP
     */
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }
}
