package com.seatone.common.exception;

import com.seatone.common.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import javax.servlet.http.HttpServletRequest;
import java.util.stream.Collectors;

/**
 * 全局异常处理器
 * 统一处理系统中各类异常，返回标准化的错误响应
 * 使用 @RestControllerAdvice 注解对所有 Controller 进行全局异常拦截
 *
 * @author seatone
 * @since 2026-06-22
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 业务异常处理
     *
     * @param e       业务异常
     * @param request HTTP请求
     * @return 错误响应结果
     */
    @ExceptionHandler(BusinessException.class)
    public Result<Void> handleBusinessException(BusinessException e, HttpServletRequest request) {
        log.warn("业务异常: uri={}, code={}, message={}", request.getRequestURI(), e.getCode(), e.getMessage());
        return Result.error(e.getCode(), e.getMessage());
    }

    /**
     * 限流异常处理（429）
     *
     * @param e       限流异常
     * @param request HTTP请求
     * @return 错误响应结果，状态码 429
     */
    @ExceptionHandler(RateLimitException.class)
    public Result<Void> handleRateLimitException(RateLimitException e, HttpServletRequest request) {
        log.warn("限流异常: uri={}, message={}", request.getRequestURI(), e.getMessage());
        return Result.error(429, e.getMessage());
    }

    /**
     * 权限不足异常处理（403）
     *
     * @param e       权限异常
     * @param request HTTP请求
     * @return 错误响应结果，状态码 403
     */
    @ExceptionHandler(AccessDeniedException.class)
    public Result<Void> handleAccessDeniedException(AccessDeniedException e, HttpServletRequest request) {
        log.warn("权限不足: uri={}, message={}", request.getRequestURI(), e.getMessage());
        return Result.error(403, "权限不足，无法访问该资源");
    }

    /**
     * 参数校验异常处理（@Validated，400）
     * 返回详细的字段校验错误信息
     *
     * @param e       参数校验异常
     * @param request HTTP请求
     * @return 错误响应结果，状态码 400
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<Void> handleMethodArgumentNotValidException(MethodArgumentNotValidException e, HttpServletRequest request) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                .collect(Collectors.joining("; "));
        log.warn("参数校验失败: uri={}, errors={}", request.getRequestURI(), message);
        return Result.error(400, message);
    }

    /**
     * 参数绑定异常处理（BindException，400）
     * 返回详细的字段校验错误信息
     *
     * @param e       参数绑定异常
     * @param request HTTP请求
     * @return 错误响应结果，状态码 400
     */
    @ExceptionHandler(BindException.class)
    public Result<Void> handleBindException(BindException e, HttpServletRequest request) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                .collect(Collectors.joining("; "));
        log.warn("参数绑定失败: uri={}, errors={}", request.getRequestURI(), message);
        return Result.error(400, message);
    }

    /**
     * 文件上传大小超限异常处理（400）
     *
     * @param e       文件上传大小超限异常
     * @param request HTTP请求
     * @return 错误响应结果，状态码 400
     */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public Result<Void> handleMaxUploadSizeExceededException(MaxUploadSizeExceededException e, HttpServletRequest request) {
        log.warn("文件上传大小超限: uri={}, message={}", request.getRequestURI(), e.getMessage());
        return Result.error(400, "上传文件大小超出限制");
    }

    /**
     * 文件上传相关异常处理（400）
     *
     * @param e       文件上传异常
     * @param request HTTP请求
     * @return 错误响应结果，状态码 400
     */
    @ExceptionHandler(org.springframework.web.multipart.MultipartException.class)
    public Result<Void> handleMultipartException(org.springframework.web.multipart.MultipartException e, HttpServletRequest request) {
        log.warn("文件上传异常: uri={}, message={}", request.getRequestURI(), e.getMessage());
        return Result.error(400, "文件上传失败: " + e.getMessage());
    }

    /**
     * 非法参数异常处理（400）
     *
     * @param e       非法参数异常
     * @param request HTTP请求
     * @return 错误响应结果，状态码 400
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public Result<Void> handleIllegalArgumentException(IllegalArgumentException e, HttpServletRequest request) {
        log.warn("非法参数: uri={}, message={}", request.getRequestURI(), e.getMessage());
        return Result.error(400, e.getMessage());
    }

    /**
     * 非法状态异常处理（400）
     *
     * @param e       非法状态异常
     * @param request HTTP请求
     * @return 错误响应结果，状态码 400
     */
    @ExceptionHandler(IllegalStateException.class)
    public Result<Void> handleIllegalStateException(IllegalStateException e, HttpServletRequest request) {
        log.warn("非法状态: uri={}, message={}", request.getRequestURI(), e.getMessage());
        return Result.error(400, e.getMessage());
    }

    /**
     * 数组越界异常处理（500）
     *
     * @param e       数组越界异常
     * @param request HTTP请求
     * @return 错误响应结果，状态码 500
     */
    @ExceptionHandler(ArrayIndexOutOfBoundsException.class)
    public Result<Void> handleArrayIndexOutOfBoundsException(ArrayIndexOutOfBoundsException e, HttpServletRequest request) {
        log.error("数组越界: uri={}", request.getRequestURI(), e);
        return Result.error(500, "系统内部错误");
    }

    /**
     * 空指针异常处理（500）
     *
     * @param e       空指针异常
     * @param request HTTP请求
     * @return 错误响应结果，状态码 500
     */
    @ExceptionHandler(NullPointerException.class)
    public Result<Void> handleNullPointerException(NullPointerException e, HttpServletRequest request) {
        log.error("空指针异常: uri={}", request.getRequestURI(), e);
        return Result.error(500, "系统内部错误");
    }

    /**
     * 兜底异常处理（500）
     * 捕获所有未被特定处理的异常
     *
     * @param e       异常
     * @param request HTTP请求
     * @return 错误响应结果，状态码 500
     */
    @ExceptionHandler(Exception.class)
    public Result<Void> handleException(Exception e, HttpServletRequest request) {
        log.error("系统异常: uri={}", request.getRequestURI(), e);
        return Result.error("系统繁忙，请稍后重试");
    }
}
