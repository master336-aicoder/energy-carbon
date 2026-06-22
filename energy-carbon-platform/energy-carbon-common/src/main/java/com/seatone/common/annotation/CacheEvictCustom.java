package com.seatone.common.annotation;

import java.lang.annotation.*;

/**
 * 自定义缓存清除注解
 * 支持按前缀批量清除缓存
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CacheEvictCustom {

    /**
     * 缓存Key的前缀
     */
    String prefix() default "";

    /**
     * 缓存Key（支持SpEL表达式）
     */
    String key() default "";

    /**
     * 是否清除所有匹配前缀的缓存
     */
    boolean allKeys() default false;

    /**
     * 缓存描述（用于日志）
     */
    String description() default "";
}
