package com.seatone.common.aspect;

import com.alibaba.fastjson2.JSON;
import com.seatone.common.annotation.CacheEvictCustom;
import com.seatone.common.utils.RedisCache;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

/**
 * 缓存切面
 * 基于 Redis 实现方法级别的缓存读写
 * 支持 @CacheableCustom 缓存读取和 @CacheEvictCustom 缓存清除注解
 * 使用 SpEL 表达式解析方法参数构建缓存Key
 *
 * @author seatone
 * @since 2026-06-22
 */
@Slf4j
@Aspect
@Component
public class CacheAspect {

    /**
     * Redis缓存工具
     */
    @Autowired
    private RedisCache redisCache;

    /**
     * SpEL表达式解析器
     */
    private final SpelExpressionParser parser = new SpelExpressionParser();

    /**
     * 参数名称发现器
     */
    private final DefaultParameterNameDiscoverer nameDiscoverer = new DefaultParameterNameDiscoverer();

    /**
     * 缓存查询切面
     * 先从缓存获取，缓存不存在则执行方法并写入缓存
     *
     * @param joinPoint 切点
     * @return 缓存值或方法执行结果
     * @throws Throwable 方法执行异常
     */
    @Around("@annotation(com.seatone.common.annotation.CacheableCustom)")
    public Object aroundCacheable(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        com.seatone.common.annotation.CacheableCustom cacheable = method.getAnnotation(com.seatone.common.annotation.CacheableCustom.class);

        // 构建缓存Key
        String cacheKey = buildCacheKey(cacheable.prefix(), cacheable.key(), method, joinPoint);

        // 尝试从缓存获取
        try {
            Object cachedValue = redisCache.getCacheObject(cacheKey);
            if (cachedValue != null) {
                log.debug("缓存命中: key={}, desc={}", cacheKey, cacheable.description());
                return cachedValue;
            }
        } catch (Exception e) {
            log.warn("读取缓存异常: key={}, error={}", cacheKey, e.getMessage());
        }

        // 执行目标方法
        Object result = joinPoint.proceed();

        // 写入缓存
        try {
            if (result != null || cacheable.cacheNull()) {
                redisCache.setCacheObject(cacheKey, result, (int) cacheable.ttl(), cacheable.timeUnit());
                log.debug("写入缓存: key={}, ttl={} {}, desc={}", cacheKey, cacheable.ttl(), cacheable.timeUnit(), cacheable.description());
            }
        } catch (Exception e) {
            log.warn("写入缓存异常: key={}, error={}", cacheKey, e.getMessage());
        }

        return result;
    }

    /**
     * 缓存清除切面
     * 先执行方法，成功后清除指定缓存
     *
     * @param joinPoint 切点
     * @return 方法执行结果
     * @throws Throwable 方法执行异常
     */
    @Around("@annotation(com.seatone.common.annotation.CacheEvictCustom)")
    public Object aroundCacheEvict(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        CacheEvictCustom cacheEvict = method.getAnnotation(CacheEvictCustom.class);

        // 先执行方法
        Object result = joinPoint.proceed();

        // 清除缓存
        try {
            if (cacheEvict.allKeys()) {
                // 按前缀批量清除
                String prefix = cacheEvict.prefix();
                if (prefix.endsWith(":") || prefix.endsWith("*")) {
                    Collection<String> keys = redisCache.keys(prefix.endsWith(":") ? prefix + "*" : prefix);
                    if (keys != null && !keys.isEmpty()) {
                        redisCache.deleteObject(keys);
                        log.debug("批量清除缓存: prefix={}, count={}, desc={}", prefix, keys.size(), cacheEvict.description());
                    }
                }
            } else {
                String cacheKey = buildCacheKey(cacheEvict.prefix(), cacheEvict.key(), method, joinPoint);
                redisCache.deleteObject(cacheKey);
                log.debug("清除缓存: key={}, desc={}", cacheKey, cacheEvict.description());
            }
        } catch (Exception e) {
            log.warn("清除缓存异常: error={}", e.getMessage());
        }

        return result;
    }

    /**
     * 构建缓存Key
     * 支持SpEL表达式解析方法参数，默认使用 类名:方法名:参数hash
     *
     * @param prefix        缓存前缀
     * @param keyExpression SpEL Key表达式
     * @param method        方法对象
     * @param joinPoint     切点
     * @return 缓存Key
     */
    private String buildCacheKey(String prefix, String keyExpression, Method method, ProceedingJoinPoint joinPoint) {
        StringBuilder keyBuilder = new StringBuilder();

        // 添加前缀
        if (prefix != null && !prefix.isEmpty()) {
            keyBuilder.append(prefix);
            if (!prefix.endsWith(":")) {
                keyBuilder.append(":");
            }
        }

        // 解析Key表达式
        if (keyExpression != null && !keyExpression.isEmpty()) {
            // 尝试SpEL表达式解析
            try {
                String[] paramNames = nameDiscoverer.getParameterNames(method);
                EvaluationContext context = new StandardEvaluationContext();
                if (paramNames != null) {
                    Object[] args = joinPoint.getArgs();
                    for (int i = 0; i < paramNames.length; i++) {
                        context.setVariable(paramNames[i], args[i]);
                    }
                }
                Expression expression = parser.parseExpression(keyExpression);
                Object value = expression.getValue(context);
                if (value != null) {
                    keyBuilder.append(value.toString());
                } else {
                    keyBuilder.append(keyExpression);
                }
            } catch (Exception e) {
                // SpEL解析失败，直接使用表达式
                keyBuilder.append(keyExpression);
            }
        } else {
            // 默认Key: 类名:方法名:参数hash
            String className = method.getDeclaringClass().getSimpleName();
            String methodName = method.getName();
            String argsHash = String.valueOf(JSON.toJSONString(joinPoint.getArgs()).hashCode());
            keyBuilder.append(className).append(":").append(methodName).append(":").append(argsHash);
        }

        return keyBuilder.toString();
    }
}
