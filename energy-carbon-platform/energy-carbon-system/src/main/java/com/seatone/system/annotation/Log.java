package com.seatone.system.annotation;

import java.lang.annotation.*;

/**
 * 操作日志注解
 * 用于标记需要记录操作日志的方法
 *
 * @author seatone
 * @since 2026-06-27
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Log {

    /**
     * 模块标题
     */
    String title() default "";

    /**
     * 业务类型（0其他 1新增 2修改 3删除 4查询 5导出）
     */
    int businessType() default 0;
}
