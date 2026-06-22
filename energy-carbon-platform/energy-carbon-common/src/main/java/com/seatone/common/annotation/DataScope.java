package com.seatone.common.annotation;

import java.lang.annotation.*;

/**
 * 数据权限注解
 * 用于标记需要进行数据权限过滤的方法
 * 支持本部门、本部门及以下、自定义数据权限
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DataScope {

    /**
     * 部门表的别名
     * 用于拼接 SQL 条件
     */
    String deptAlias() default "";

    /**
     * 用户表的别名
     * 用于拼接 SQL 条件
     */
    String userAlias() default "";

    /**
     * 权限字符（用于匹配数据权限规则）
     */
    String permission() default "";
}
