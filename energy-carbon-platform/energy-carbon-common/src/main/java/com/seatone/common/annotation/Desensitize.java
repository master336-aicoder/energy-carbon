package com.seatone.common.annotation;

import java.lang.annotation.*;

/**
 * 数据脱敏注解
 * 标注在字段上，指定脱敏策略
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Desensitize {

    /**
     * 脱敏类型
     */
    DesensitizeType type() default DesensitizeType.DEFAULT;

    /**
     * 脱敏类型枚举
     */
    enum DesensitizeType {
        /** 默认：保留前2后2 */
        DEFAULT,
        /** 手机号：138****8000 */
        PHONE,
        /** 身份证号：110***********1234 */
        ID_CARD,
        /** 邮箱：a***@example.com */
        EMAIL,
        /** 地址：保留前6后4 */
        ADDRESS,
        /** 银行卡号：****1234 */
        BANK_CARD,
        /** 密码：全部隐藏 */
        PASSWORD,
        /** 姓名：保留姓 */
        NAME,
        /** 固定电话 */
        FIXED_PHONE,
        /** 不脱敏 */
        NONE
    }
}
