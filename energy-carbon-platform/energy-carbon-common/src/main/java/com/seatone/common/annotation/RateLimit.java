package com.seatone.common.annotation;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 * 接口限流注解
 * 基于Redis实现的接口访问频率限制
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RateLimit {

    /**
     * 限流Key（支持SpEL表达式）
     * 默认使用IP地址作为限流维度
     */
    String key() default "";

    /**
     * 时间窗口内允许的最大请求数
     */
    int count() default 10;

    /**
     * 时间窗口大小
     */
    long time() default 60;

    /**
     * 时间单位
     */
    TimeUnit timeUnit() default TimeUnit.SECONDS;

    /**
     * 限流提示消息
     */
    String message() default "操作过于频繁，请稍后再试";

    /**
     * 限流维度：IP、USER、GLOBAL
     */
    LimitType limitType() default LimitType.IP;

    /**
     * 限流维度枚举
     */
    enum LimitType {
        /** 按IP限流 */
        IP,
        /** 按用户限流 */
        USER,
        /** 全局限流 */
        GLOBAL
    }
}
