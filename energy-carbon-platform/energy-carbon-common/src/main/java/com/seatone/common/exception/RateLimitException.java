package com.seatone.common.exception;

/**
 * 限流异常
 * 当接口访问频率超过限制时抛出
 */
public class RateLimitException extends RuntimeException {

    public RateLimitException(String message) {
        super(message);
    }

    public RateLimitException(String message, Throwable cause) {
        super(message, cause);
    }
}
