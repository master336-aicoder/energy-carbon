package com.seatone.system.aspect;

import com.alibaba.fastjson2.JSON;
import com.seatone.common.utils.JwtUtils;
import com.seatone.system.annotation.Log;
import com.seatone.system.entity.SysLog;
import com.seatone.system.service.SysLogService;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * 操作日志 AOP 拦截器
 * 拦截带有 {@link Log} 注解的 Controller 方法，记录操作日志
 *
 * @author seatone
 * @since 2026-06-27
 */
@Slf4j
@Aspect
@Component
public class LogAspect {

    @Autowired
    private SysLogService sysLogService;

    /**
     * 用于存储每个请求的开始时间
     */
    private static final ThreadLocal<Long> START_TIME = new ThreadLocal<>();

    /**
     * 用于存储每个请求的日志对象
     */
    private static final ThreadLocal<SysLog> LOG_THREAD_LOCAL = new ThreadLocal<>();

    /**
     * 前置通知：记录请求开始时间和基础信息
     */
    @Before("@annotation(com.seatone.system.annotation.Log)")
    public void doBefore(JoinPoint joinPoint) {
        START_TIME.set(System.currentTimeMillis());

        SysLog sysLog = new SysLog();
        sysLog.setOperTime(LocalDateTime.now());

        // 获取请求信息
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            sysLog.setOperUrl(request.getRequestURI());
            sysLog.setRequestMethod(request.getMethod());
            sysLog.setOperIp(getClientIp(request));

            // 获取操作人（从 Token 中解析）
            String operName = getUsernameFromRequest(request);
            sysLog.setOperName(operName != null ? operName : "unknown");

            // 获取请求参数
            String params = getRequestParams(request, joinPoint);
            sysLog.setOperParam(params);

            // 获取浏览器和操作系统信息
            String userAgent = request.getHeader("User-Agent");
            sysLog.setUserAgent(userAgent);
            sysLog.setReferer(request.getHeader("Referer"));
            if (userAgent != null && !userAgent.isEmpty()) {
                sysLog.setBrowser(parseBrowser(userAgent));
                sysLog.setOs(parseOs(userAgent));
            }
        }

        // 获取注解信息
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Log annotation = method.getAnnotation(Log.class);
        if (annotation != null) {
            sysLog.setTitle(annotation.title());
            sysLog.setBusinessType(annotation.businessType());
        }

        // 设置方法全限定名
        String className = joinPoint.getTarget().getClass().getName();
        String methodName = method.getName();
        sysLog.setMethod(className + "." + methodName + "()");

        LOG_THREAD_LOCAL.set(sysLog);
    }

    /**
     * 返回通知：记录操作成功结果
     */
    @AfterReturning(pointcut = "@annotation(com.seatone.system.annotation.Log)", returning = "result")
    public void doAfterReturning(Object result) {
        SysLog sysLog = LOG_THREAD_LOCAL.get();
        if (sysLog == null) {
            return;
        }

        try {
            // 计算耗时
            long costTime = System.currentTimeMillis() - START_TIME.get();
            sysLog.setCostTime(costTime);
            sysLog.setStatus(0);

            // 记录返回结果（限制长度，避免过大）
            String jsonResult = JSON.toJSONString(result);
            if (jsonResult.length() > 2000) {
                jsonResult = jsonResult.substring(0, 2000) + "...";
            }
            sysLog.setJsonResult(jsonResult);

            // 保存日志
            sysLogService.addLog(sysLog);
        } catch (Exception e) {
            log.error("记录操作日志失败", e);
        } finally {
            clearThreadLocal();
        }
    }

    /**
     * 异常通知：记录操作异常信息
     */
    @AfterThrowing(pointcut = "@annotation(com.seatone.system.annotation.Log)", throwing = "ex")
    public void doAfterThrowing(Exception ex) {
        SysLog sysLog = LOG_THREAD_LOCAL.get();
        if (sysLog == null) {
            return;
        }

        try {
            // 计算耗时
            long costTime = System.currentTimeMillis() - START_TIME.get();
            sysLog.setCostTime(costTime);
            sysLog.setStatus(1);

            // 记录异常信息
            String errorMsg = ex.getMessage();
            if (errorMsg != null && errorMsg.length() > 2000) {
                errorMsg = errorMsg.substring(0, 2000);
            }
            sysLog.setErrorMsg(errorMsg);

            // 保存日志
            sysLogService.addLog(sysLog);
        } catch (Exception e) {
            log.error("记录操作日志失败", e);
        } finally {
            clearThreadLocal();
        }
    }

    /**
     * 清理 ThreadLocal
     */
    private void clearThreadLocal() {
        START_TIME.remove();
        LOG_THREAD_LOCAL.remove();
    }

    /**
     * 从请求中获取用户名
     */
    private String getUsernameFromRequest(HttpServletRequest request) {
        String token = resolveToken(request);
        if (token != null && JwtUtils.validateToken(token)) {
            try {
                return JwtUtils.getUsername(token);
            } catch (Exception e) {
                log.warn("从Token解析用户名失败: {}", e.getMessage());
            }
        }
        return null;
    }

    /**
     * 从请求头中解析 Bearer Token
     */
    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    /**
     * 获取请求参数
     */
    private String getRequestParams(HttpServletRequest request, JoinPoint joinPoint) {
        Map<String, Object> paramsMap = new HashMap<>();

        // 获取 URL 参数
        Enumeration<String> parameterNames = request.getParameterNames();
        while (parameterNames.hasMoreElements()) {
            String name = parameterNames.nextElement();
            paramsMap.put(name, request.getParameter(name));
        }

        // 获取 Body 参数（通过方法参数获取）
        Object[] args = joinPoint.getArgs();
        if (args != null && args.length > 0) {
            for (int i = 0; i < args.length; i++) {
                Object arg = args[i];
                if (arg != null && !(arg instanceof HttpServletRequest)) {
                    try {
                        paramsMap.put("arg" + i, JSON.toJSONString(arg));
                    } catch (Exception e) {
                        paramsMap.put("arg" + i, arg.toString());
                    }
                }
            }
        }

        String params = JSON.toJSONString(paramsMap);
        if (params.length() > 2000) {
            params = params.substring(0, 2000) + "...";
        }
        return params;
    }

    /**
     * 从 User-Agent 中解析浏览器信息
     */
    private String parseBrowser(String userAgent) {
        if (userAgent == null) return "Unknown";
        userAgent = userAgent.toLowerCase();
        if (userAgent.contains("edg")) return "Edge";
        if (userAgent.contains("chrome")) return "Chrome";
        if (userAgent.contains("safari") && !userAgent.contains("chrome")) return "Safari";
        if (userAgent.contains("firefox")) return "Firefox";
        if (userAgent.contains("opera") || userAgent.contains("opr")) return "Opera";
        if (userAgent.contains("trident") || userAgent.contains("msie")) return "IE";
        return "Unknown";
    }

    /**
     * 从 User-Agent 中解析操作系统信息
     */
    private String parseOs(String userAgent) {
        if (userAgent == null) return "Unknown";
        userAgent = userAgent.toLowerCase();
        if (userAgent.contains("windows nt 10")) return "Windows 10/11";
        if (userAgent.contains("windows nt 6.3")) return "Windows 8.1";
        if (userAgent.contains("windows nt 6.2")) return "Windows 8";
        if (userAgent.contains("windows nt 6.1")) return "Windows 7";
        if (userAgent.contains("macintosh") || userAgent.contains("mac os")) return "Mac OS";
        if (userAgent.contains("linux") && userAgent.contains("android")) return "Android";
        if (userAgent.contains("linux")) return "Linux";
        if (userAgent.contains("iphone") || userAgent.contains("ipad")) return "iOS";
        return "Unknown";
    }

    /**
     * 获取客户端真实 IP 地址
     */
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
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
}
