package com.seatone.common.annotation;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 * 自定义缓存注解
 * 支持自定义TTL和缓存Key
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CacheableCustom {

    /**
     * 缓存Key的前缀
     */
    String prefix() default "";

    /**
     * 缓存Key（支持SpEL表达式，如 #userId, #dictType）
     * 如果不指定，则使用 类名:方法名:参数hash
     */
    String key() default "";

    /**
     * 缓存过期时间
     */
    long ttl() default 30;

    /**
     * 时间单位
     */
    TimeUnit timeUnit() default TimeUnit.MINUTES;

    /**
     * 是否缓存空值，防止缓存穿透
     */
    boolean cacheNull() default true;

    /**
     * 缓存描述（用于日志）
     */
    String description() default "";
}
