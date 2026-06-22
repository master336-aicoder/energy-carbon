package com.seatone.common.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.IOException;
import java.util.regex.Pattern;

/**
 * XSS过滤器
 * 过滤请求参数中的XSS攻击代码，防止跨站脚本攻击
 * 通过正则表达式匹配并清除常见的XSS攻击模式
 * 过滤顺序：2（在SQL注入过滤器之后执行）
 *
 * @author seatone
 * @since 2026-06-22
 */
@Slf4j
@Order(2)
@WebFilter(urlPatterns = "/*", filterName = "xssFilter")
public class XssFilter implements Filter {

    /**
     * XSS攻击模式正则表达式
     */
    private static final Pattern[] XSS_PATTERNS = {
            // Script标签
            Pattern.compile("<script.*?>.*?</script>", Pattern.CASE_INSENSITIVE),
            Pattern.compile("</script>", Pattern.CASE_INSENSITIVE),
            Pattern.compile("<script.*?>", Pattern.CASE_INSENSITIVE),
            // JavaScript表达式
            Pattern.compile("javascript:", Pattern.CASE_INSENSITIVE),
            Pattern.compile("vbscript:", Pattern.CASE_INSENSITIVE),
            Pattern.compile("on(load|click|mouseover|submit|focus|blur)\\s*=", Pattern.CASE_INSENSITIVE),
            // HTML事件
            Pattern.compile("eval\\((.*?)\\)", Pattern.CASE_INSENSITIVE),
            Pattern.compile("expression\\((.*?)\\)", Pattern.CASE_INSENSITIVE),
            // iframe/embed/object
            Pattern.compile("<iframe.*?>.*?</iframe>", Pattern.CASE_INSENSITIVE),
            Pattern.compile("<embed.*?>", Pattern.CASE_INSENSITIVE),
            Pattern.compile("<object.*?>.*?</object>", Pattern.CASE_INSENSITIVE),
            // HTML标签
            Pattern.compile("<[^>]*>", Pattern.CASE_INSENSITIVE),
    };

    /**
     * 过滤器初始化
     *
     * @param filterConfig 过滤器配置
     */
    @Override
    public void init(FilterConfig filterConfig) {
        log.info("XSS过滤器初始化完成");
    }

    /**
     * 执行过滤
     * 对请求参数进行XSS攻击代码清除
     *
     * @param request  请求对象
     * @param response 响应对象
     * @param chain    过滤器链
     * @throws IOException      IO异常
     * @throws ServletException Servlet异常
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        XssHttpServletRequestWrapper wrapper = new XssHttpServletRequestWrapper(httpRequest);
        chain.doFilter(wrapper, response);
    }

    /**
     * 过滤器销毁
     */
    @Override
    public void destroy() {
        log.info("XSS过滤器销毁");
    }

    /**
     * 清除XSS攻击代码
     *
     * @param value 原始字符串
     * @return 清除XSS后的字符串
     */
    public static String cleanXss(String value) {
        if (value == null || value.isEmpty()) {
            return value;
        }
        String cleanValue = value;
        for (Pattern pattern : XSS_PATTERNS) {
            cleanValue = pattern.matcher(cleanValue).replaceAll("");
        }
        // 替换特殊字符
        cleanValue = cleanValue.replaceAll("\r", "");
        cleanValue = cleanValue.replaceAll("\n", "");
        cleanValue = cleanValue.replaceAll("\t", "");
        return cleanValue.trim();
    }

    /**
     * XSS请求包装器
     * 对请求参数、请求头进行XSS过滤
     */
    static class XssHttpServletRequestWrapper extends HttpServletRequestWrapper {

        /**
         * 构造方法
         *
         * @param request HTTP请求
         */
        public XssHttpServletRequestWrapper(HttpServletRequest request) {
            super(request);
        }

        /**
         * 获取参数值数组（清除XSS）
         *
         * @param name 参数名
         * @return 清除XSS后的参数值数组
         */
        @Override
        public String[] getParameterValues(String name) {
            String[] values = super.getParameterValues(name);
            if (values == null) {
                return null;
            }
            String[] cleanValues = new String[values.length];
            for (int i = 0; i < values.length; i++) {
                cleanValues[i] = cleanXss(values[i]);
            }
            return cleanValues;
        }

        /**
         * 获取单个参数值（清除XSS）
         *
         * @param name 参数名
         * @return 清除XSS后的参数值
         */
        @Override
        public String getParameter(String name) {
            String value = super.getParameter(name);
            return cleanXss(value);
        }

        /**
         * 获取请求头（清除XSS）
         *
         * @param name 请求头名
         * @return 清除XSS后的请求头值
         */
        @Override
        public String getHeader(String name) {
            String value = super.getHeader(name);
            return cleanXss(value);
        }
    }
}
