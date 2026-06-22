package com.seatone.common.result;

import lombok.Data;

/**
 * 通用响应结果封装类
 * 用于统一封装接口返回的数据结构，包含状态码、消息和数据
 *
 * @param <T> 响应数据类型
 * @author seatone
 * @since 2026-06-22
 */
@Data
public class Result<T> {

    /**
     * 响应状态码，200表示成功，其他表示失败
     */
    private Integer code;

    /**
     * 响应消息，成功或失败的提示信息
     */
    private String message;

    /**
     * 响应数据，成功时返回的具体业务数据
     */
    private T data;

    /**
     * 返回成功响应（无数据）
     *
     * @param <T> 响应数据类型
     * @return 成功响应结果，code=200，message="操作成功"
     */
    public static <T> Result<T> ok() {
        return ok(null);
    }

    /**
     * 返回成功响应（带数据）
     *
     * @param data 响应数据
     * @param <T>  响应数据类型
     * @return 成功响应结果，code=200，message="操作成功"
     */
    public static <T> Result<T> ok(T data) {
        Result<T> result = new Result<>();
        result.setCode(200);
        result.setMessage("操作成功");
        result.setData(data);
        return result;
    }

    /**
     * 返回错误响应（默认状态码500）
     *
     * @param message 错误消息
     * @param <T>     响应数据类型
     * @return 错误响应结果，code=500
     */
    public static <T> Result<T> error(String message) {
        return error(500, message);
    }

    /**
     * 返回错误响应（自定义状态码）
     *
     * @param code    错误状态码
     * @param message 错误消息
     * @param <T>     响应数据类型
     * @return 错误响应结果
     */
    public static <T> Result<T> error(int code, String message) {
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setMessage(message);
        return result;
    }
}
