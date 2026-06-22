package com.seatone.common.utils;

import com.seatone.common.annotation.Desensitize;
import com.seatone.common.annotation.Desensitize.DesensitizeType;

import java.lang.reflect.Field;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 数据脱敏工具类
 * 支持手机号、身份证、邮箱、银行卡号等多种脱敏策略
 */
public class DesensitizeUtils {

    /**
     * 手机号正则
     */
    private static final Pattern PHONE_PATTERN = Pattern.compile("(\\d{3})\\d{4}(\\d{4})");

    /**
     * 身份证号正则（15位或18位）
     */
    private static final Pattern ID_CARD_PATTERN = Pattern.compile("(\\d{3})\\d{11}(\\d{4})");

    /**
     * 邮箱正则
     */
    private static final Pattern EMAIL_PATTERN = Pattern.compile("(\\w{1})\\w+@(\\w+\\.\\w+)");

    /**
     * 银行卡号正则
     */
    private static final Pattern BANK_CARD_PATTERN = Pattern.compile("\\d{4}(\\d{8,12})\\d{4}");

    /**
     * 根据脱敏类型对字符串进行脱敏
     */
    public static String desensitize(String value, DesensitizeType type) {
        if (value == null || value.isEmpty()) {
            return value;
        }

        switch (type) {
            case PHONE:
                return desensitizePhone(value);
            case ID_CARD:
                return desensitizeIdCard(value);
            case EMAIL:
                return desensitizeEmail(value);
            case ADDRESS:
                return desensitizeAddress(value);
            case BANK_CARD:
                return desensitizeBankCard(value);
            case PASSWORD:
                return "******";
            case NAME:
                return desensitizeName(value);
            case FIXED_PHONE:
                return desensitizeFixedPhone(value);
            case NONE:
                return value;
            case DEFAULT:
            default:
                return desensitizeDefault(value);
        }
    }

    /**
     * 默认脱敏：保留前2后2
     */
    public static String desensitizeDefault(String value) {
        if (value == null || value.length() <= 4) {
            return value;
        }
        return mask(value, 2, 2);
    }

    /**
     * 手机号脱敏：138****8000
     */
    public static String desensitizePhone(String phone) {
        if (phone == null || phone.length() != 11) {
            return phone;
        }
        Matcher matcher = PHONE_PATTERN.matcher(phone);
        if (matcher.matches()) {
            return matcher.group(1) + "****" + matcher.group(2);
        }
        return mask(phone, 3, 4);
    }

    /**
     * 身份证号脱敏：110***********1234
     */
    public static String desensitizeIdCard(String idCard) {
        if (idCard == null || (idCard.length() != 15 && idCard.length() != 18)) {
            return idCard;
        }
        Matcher matcher = ID_CARD_PATTERN.matcher(idCard);
        if (matcher.matches()) {
            return matcher.group(1) + "***********" + matcher.group(2);
        }
        return mask(idCard, 3, 4);
    }

    /**
     * 邮箱脱敏：a***@example.com
     */
    public static String desensitizeEmail(String email) {
        if (email == null || !email.contains("@")) {
            return email;
        }
        Matcher matcher = EMAIL_PATTERN.matcher(email);
        if (matcher.matches()) {
            return matcher.group(1) + "***@" + matcher.group(2);
        }
        int atIndex = email.indexOf("@");
        return mask(email.substring(0, atIndex), 1, 0) + email.substring(atIndex);
    }

    /**
     * 地址脱敏：保留前6后4
     */
    public static String desensitizeAddress(String address) {
        if (address == null || address.length() <= 10) {
            return address;
        }
        return mask(address, 6, 4);
    }

    /**
     * 银行卡号脱敏：****1234
     */
    public static String desensitizeBankCard(String bankCard) {
        if (bankCard == null || bankCard.length() < 8) {
            return bankCard;
        }
        return "****" + bankCard.substring(bankCard.length() - 4);
    }

    /**
     * 姓名脱敏：保留姓
     */
    public static String desensitizeName(String name) {
        if (name == null || name.isEmpty()) {
            return name;
        }
        if (name.length() == 1) {
            return name;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(name.charAt(0));
        for (int i = 1; i < name.length(); i++) {
            sb.append('*');
        }
        return sb.toString();
    }

    /**
     * 固定电话脱敏
     */
    public static String desensitizeFixedPhone(String phone) {
        if (phone == null || phone.length() < 7) {
            return phone;
        }
        return mask(phone, 3, 4);
    }

    /**
     * 对对象进行脱敏处理
     * 扫描对象中标注了 @Desensitize 注解的字段并脱敏
     */
    public static <T> T desensitizeObject(T obj) {
        if (obj == null) {
            return null;
        }
        try {
            Class<?> clazz = obj.getClass();
            for (Field field : clazz.getDeclaredFields()) {
                Desensitize annotation = field.getAnnotation(Desensitize.class);
                if (annotation != null && annotation.type() != DesensitizeType.NONE) {
                    field.setAccessible(true);
                    Object value = field.get(obj);
                    if (value instanceof String) {
                        String desensitized = desensitize((String) value, annotation.type());
                        field.set(obj, desensitized);
                    }
                }
            }
        } catch (IllegalAccessException e) {
            // 忽略访问异常
        }
        return obj;
    }

    /**
     * 通用遮罩方法
     * @param value 原始值
     * @param prefixLen 保留前缀长度
     * @param suffixLen 保留后缀长度
     */
    private static String mask(String value, int prefixLen, int suffixLen) {
        if (value == null) {
            return null;
        }
        int len = value.length();
        if (len <= prefixLen + suffixLen) {
            return value;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(value, 0, prefixLen);
        for (int i = prefixLen; i < len - suffixLen; i++) {
            sb.append('*');
        }
        sb.append(value, len - suffixLen, len);
        return sb.toString();
    }
}
