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
 * 通过正则表达式匹配常见的SQL注入特征，对匹配到的参数值进行清空处理
 * 过滤顺序：1（最先执行）
 *
 * @author seatone
 * @since 2026-06-22
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

    /**
     * 过滤器初始化
     *
     * @param filterConfig 过滤器配置
     */
    @Override
    public void init(FilterConfig filterConfig) {
        log.info("SQL注入过滤器初始化完成");
    }

    /**
     * 执行过滤
     * 对请求参数进行SQL注入检测和过滤
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
        SqlInjectionHttpServletRequestWrapper wrapper = new SqlInjectionHttpServletRequestWrapper(httpRequest);
        chain.doFilter(wrapper, response);
    }

    /**
     * 过滤器销毁
     */
    @Override
    public void destroy() {
        log.info("SQL注入过滤器销毁");
    }

    /**
     * 检查字符串是否包含SQL注入关键词
     *
     * @param value 待检查的字符串
     * @return true=包含SQL注入关键词
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
     * 对请求参数、请求头进行SQL注入过滤
     */
    static class SqlInjectionHttpServletRequestWrapper extends HttpServletRequestWrapper {

        /**
         * 构造方法
         *
         * @param request HTTP请求
         */
        public SqlInjectionHttpServletRequestWrapper(HttpServletRequest request) {
            super(request);
        }

        /**
         * 获取参数值数组（过滤SQL注入）
         *
         * @param name 参数名
         * @return 过滤后的参数值数组
         */
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

        /**
         * 获取单个参数值（过滤SQL注入）
         *
         * @param name 参数名
         * @return 过滤后的参数值
         */
        @Override
        public String getParameter(String name) {
            String value = super.getParameter(name);
            if (containsSqlInjection(value)) {
                log.warn("SQL注入拦截: 参数名={}, 参数值已清空", name);
                return "";
            }
            return value;
        }

        /**
         * 获取请求头（过滤SQL注入）
         *
         * @param name 请求头名
         * @return 过滤后的请求头值
         */
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
