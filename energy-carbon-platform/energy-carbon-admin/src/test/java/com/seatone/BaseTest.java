package com.seatone;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.Mockito;
import org.mockito.quality.Strictness;

/**
 * 测试基类
 * 配置测试上下文，提供通用的Mock设置
 */
@ExtendWith(MockitoExtension.class)
public abstract class BaseTest {

    /**
     * 通用断言辅助方法
     */
    protected <T> T verifyNotNull(T object, String message) {
        assert object != null : message;
        return object;
    }

    /**
     * 通用断言辅助方法
     */
    protected void assertEquals(Object expected, Object actual, String message) {
        assert expected.equals(actual) : message + " - expected: " + expected + ", actual: " + actual;
    }
}
