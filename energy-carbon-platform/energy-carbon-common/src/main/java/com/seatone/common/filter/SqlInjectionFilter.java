package com.seatone.common.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * SQL注入过滤器
 * 拦截请求参数中的SQL关键词，防止SQL注入攻击
 */
@Slf4j
@Order(1)
@WebFilter(urlPatterns = "/*", filterName = "sqlInjectionFilter")
public class SqlInjectionFilter implements Filter {

    /**
     * SQL注入关键词正则表达式
     */
    private static final String[] SQL_KEYWORDS = {
            "('.+--)", "--", ";", "/\\*.*\\*/",
            "(?i)\\b(select|insert|update|delete|drop|alter|truncate|exec|execute|xp_|sp_|0x)",
            "(?i)\\b(union|all|and|or|not|having|group by|order by|limit|offset)\\b",
            "(?i)\\b(waitfor|delay|benchmark|sleep|shutdown)\\b",
            "(?i)\\b(information_schema|mysql|sys_tables|syscolumns)\\b",
            "(?i)\\b(char|concat|varchar|nvarchar|cast|convert)\\s*\\(",
            "'\\s*(or|and)\\s*'.*='"
    };

    @Override
    public void init(FilterConfig filterConfig) {
        log.info("SQL注入过滤器初始化完成");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        SqlInjectionHttpServletRequestWrapper wrapper = new SqlInjectionHttpServletRequestWrapper(httpRequest);
        chain.doFilter(wrapper, response);
    }

    @Override
    public void destroy() {
        log.info("SQL注入过滤器销毁");
    }

    /**
     * 检查字符串是否包含SQL注入关键词
     */
    public static boolean containsSqlInjection(String value) {
        if (value == null || value.isEmpty()) {
            return false;
        }
        for (String keyword : SQL_KEYWORDS) {
            Pattern pattern = Pattern.compile(keyword);
            Matcher matcher = pattern.matcher(value);
            if (matcher.find()) {
                log.warn("检测到SQL注入关键词: value={}, pattern={}", value, keyword);
                return true;
            }
        }
        return false;
    }

    /**
     * SQL注入请求包装器
     */
    static class SqlInjectionHttpServletRequestWrapper extends HttpServletRequestWrapper {

        public SqlInjectionHttpServletRequestWrapper(HttpServletRequest request) {
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
                if (containsSqlInjection(values[i])) {
                    cleanValues[i] = "";
                    log.warn("SQL注入拦截: 参数名={}, 参数值已清空", name);
                } else {
                    cleanValues[i] = values[i];
                }
            }
            return cleanValues;
        }

        @Override
        public String getParameter(String name) {
            String value = super.getParameter(name);
            if (containsSqlInjection(value)) {
                log.warn("SQL注入拦截: 参数名={}, 参数值已清空", name);
                return "";
            }
            return value;
        }

        @Override
        public String getHeader(String name) {
            String value = super.getHeader(name);
            if (containsSqlInjection(value)) {
                log.warn("SQL注入拦截: 请求头名={}, 值已清空", name);
                return "";
            }
            return value;
        }
    }
}
