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
 * 过滤请求参数中的XSS攻击代码
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

    @Override
    public void init(FilterConfig filterConfig) {
        log.info("XSS过滤器初始化完成");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        XssHttpServletRequestWrapper wrapper = new XssHttpServletRequestWrapper(httpRequest);
        chain.doFilter(wrapper, response);
    }

    @Override
    public void destroy() {
        log.info("XSS过滤器销毁");
    }

    /**
     * 清除XSS攻击代码
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
     */
    static class XssHttpServletRequestWrapper extends HttpServletRequestWrapper {

        public XssHttpServletRequestWrapper(HttpServletRequest request) {
            super(request);
        }

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

        @Override
        public String getParameter(String name) {
            String value = super.getParameter(name);
            return cleanXss(value);
        }

        @Override
        public String getHeader(String name) {
            String value = super.getHeader(name);
            return cleanXss(value);
        }
    }
}
