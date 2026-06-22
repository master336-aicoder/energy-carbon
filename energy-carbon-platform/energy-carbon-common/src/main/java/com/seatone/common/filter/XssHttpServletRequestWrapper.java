package com.seatone.common.filter;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.*;

/**
 * XSS过滤请求包装器
 * 用于包装HttpServletRequest，过滤请求体中的XSS攻击代码
 */
public class XssHttpServletRequestWrapper extends HttpServletRequestWrapper {

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
            cleanValues[i] = XssFilter.cleanXss(values[i]);
        }
        return cleanValues;
    }

    @Override
    public String getParameter(String name) {
        String value = super.getParameter(name);
        return XssFilter.cleanXss(value);
    }

    @Override
    public String getHeader(String name) {
        String value = super.getHeader(name);
        return XssFilter.cleanXss(value);
    }

    @Override
    public String getQueryString() {
        String value = super.getQueryString();
        return XssFilter.cleanXss(value);
    }
}
