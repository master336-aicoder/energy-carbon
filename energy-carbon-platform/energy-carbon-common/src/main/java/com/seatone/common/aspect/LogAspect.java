package com.seatone.common.aspect;

import com.alibaba.fastjson2.JSON;
import com.seatone.common.annotation.Log;
import com.seatone.common.utils.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 操作日志记录切面
 * 自动记录标注了 @Log 注解的方法的操作日志
 * 使用异步线程池记录日志，避免影响主业务流程性能
 * 支持记录请求参数、返回结果、执行耗时、异常信息等
 *
 * @author seatone
 * @since 2026-06-22
 */
@Slf4j
@Aspect
@Component
public class LogAspect {

    /**
     * 排除敏感字段，不记录到日志中
     */
    private static final String[] EXCLUDE_FIELDS = {"password", "oldPassword", "newPassword", "confirmPassword"};

    /**
     * 记录操作日志的线程本地变量
     */
    private static final ThreadLocal<Map<String, Object>> LOG_THREAD_LOCAL = new ThreadLocal<>();

    /**
     * 方法执行前：记录请求信息
     * 包括请求URL、请求方法、IP地址、请求参数、操作用户等
     *
     * @param joinPoint 切点
     */
    @Before("@annotation(com.seatone.common.annotation.Log)")
    public void doBefore(JoinPoint joinPoint) {
        try {
            Map<String, Object> operLog = new HashMap<>();
            operLog.put("startTime", System.currentTimeMillis());
            operLog.put("operTime", LocalDateTime.now());

            // 获取请求信息
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                operLog.put("operUrl", request.getRequestURI());
                operLog.put("requestMethod", request.getMethod());
                operLog.put("operIp", getIpAddress(request));
                operLog.put("operParam", getRequestParams(joinPoint));
            }

            // 获取当前登录用户
            Long userId = SecurityUtils.getCurrentUserId();
            String username = SecurityUtils.getCurrentUsername();
            operLog.put("operName", username != null ? username : "anonymous");
            operLog.put("userId", userId);

            // 获取注解信息
            Log controllerLog = getAnnotation(joinPoint);
            if (controllerLog != null) {
                operLog.put("title", controllerLog.title());
                operLog.put("businessType", controllerLog.businessType().name());
            }

            // 获取方法名
            String className = joinPoint.getTarget().getClass().getName();
            String methodName = joinPoint.getSignature().getName();
            operLog.put("method", className + "." + methodName + "()");

            LOG_THREAD_LOCAL.set(operLog);
        } catch (Exception e) {
            log.error("操作日志记录前置处理异常: {}", e.getMessage());
        }
    }

    /**
     * 方法正常返回后：记录返回结果
     * 计算执行耗时，异步保存日志
     *
     * @param joinPoint  切点
     * @param jsonResult 方法返回结果
     */
    @AfterReturning(pointcut = "@annotation(com.seatone.common.annotation.Log)", returning = "jsonResult")
    public void doAfterReturning(JoinPoint joinPoint, Object jsonResult) {
        try {
            Map<String, Object> operLog = LOG_THREAD_LOCAL.get();
            if (operLog == null) {
                return;
            }

            operLog.put("status", 1); // 1-正常
            operLog.put("errorMsg", "");

            // 记录返回结果
            Log controllerLog = getAnnotation(joinPoint);
            if (controllerLog != null && controllerLog.isSaveResponseData() && jsonResult != null) {
                try {
                    String resultStr = JSON.toJSONString(jsonResult);
                    // 限制返回结果长度，防止日志过大
                    if (resultStr.length() > 2000) {
                        resultStr = resultStr.substring(0, 2000) + "...";
                    }
                    operLog.put("jsonResult", resultStr);
                } catch (Exception e) {
                    operLog.put("jsonResult", "序列化失败");
                }
            }

            // 计算执行耗时
            Long startTime = (Long) operLog.get("startTime");
            long costTime = System.currentTimeMillis() - startTime;
            operLog.put("costTime", costTime);

            // 异步保存日志
            saveLog(operLog);
        } catch (Exception e) {
            log.error("操作日志记录后置处理异常: {}", e.getMessage());
        } finally {
            LOG_THREAD_LOCAL.remove();
        }
    }

    /**
     * 方法抛出异常后：记录异常信息
     * 计算执行耗时，异步保存日志
     *
     * @param joinPoint 切点
     * @param e         异常
     */
    @AfterThrowing(pointcut = "@annotation(com.seatone.common.annotation.Log)", throwing = "e")
    public void doAfterThrowing(JoinPoint joinPoint, Exception e) {
        try {
            Map<String, Object> operLog = LOG_THREAD_LOCAL.get();
            if (operLog == null) {
                return;
            }

            operLog.put("status", 0); // 0-异常
            operLog.put("errorMsg", e.getMessage());
            operLog.put("jsonResult", "");

            // 计算执行耗时
            Long startTime = (Long) operLog.get("startTime");
            long costTime = System.currentTimeMillis() - startTime;
            operLog.put("costTime", costTime);

            // 异步保存日志
            saveLog(operLog);
        } catch (Exception ex) {
            log.error("操作日志记录异常处理异常: {}", ex.getMessage());
        } finally {
            LOG_THREAD_LOCAL.remove();
        }
    }

    /**
     * 异步保存操作日志
     *
     * @param operLog 操作日志信息
     */
    @Async("logExecutor")
    public void saveLog(Map<String, Object> operLog) {
        try {
            // 这里可以将日志保存到数据库或发送到消息队列
            // 示例：打印到日志文件
            String logStr = JSON.toJSONString(operLog);
            log.info("[操作日志] {}", logStr);

            // TODO: 实际项目中可以注入 SysOperLogService 保存到数据库
            // SysOperLog sysOperLog = convertToEntity(operLog);
            // sysOperLogService.save(sysOperLog);
        } catch (Exception e) {
            log.error("保存操作日志异常: {}", e.getMessage());
        }
    }

    /**
     * 获取请求参数
     * 过滤敏感字段并限制参数长度
     *
     * @param joinPoint 切点
     * @return 参数字符串
     */
    private String getRequestParams(JoinPoint joinPoint) {
        try {
            String params = JSON.toJSONString(joinPoint.getArgs());
            // 过滤敏感字段
            for (String field : EXCLUDE_FIELDS) {
                params = params.replaceAll("\"" + field + "\":\"[^\"]*\"", "\"" + field + "\":\"***\"");
            }
            // 限制参数长度
            if (params.length() > 2000) {
                params = params.substring(0, 2000) + "...";
            }
            return params;
        } catch (Exception e) {
            return "参数获取失败";
        }
    }

    /**
     * 获取客户端 IP 地址
     * 支持多级代理环境
     *
     * @param request HTTP 请求
     * @return IP 地址
     */
    private String getIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // 多个代理情况，取第一个 IP
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }

    /**
     * 获取方法上的 @Log 注解
     *
     * @param joinPoint 切点
     * @return @Log 注解
     */
    private Log getAnnotation(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        return method.getAnnotation(Log.class);
    }
}
