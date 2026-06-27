package com.seatone.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.seatone.system.entity.SysLog;
import com.seatone.system.mapper.SysLogMapper;
import com.seatone.system.service.SysLogService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 操作日志 Service 实现
 *
 * @author seatone
 * @since 2026-06-27
 */
@Service
public class SysLogServiceImpl extends ServiceImpl<SysLogMapper, SysLog> implements SysLogService {

    @Override
    public void addLog(SysLog log) {
        baseMapper.insert(log);
    }

    @Override
    public Page<SysLog> listLogs(Long current, Long size, String operName, String title,
                                 Integer businessType, Integer status,
                                 String startTime, String endTime) {
        Page<SysLog> page = new Page<>(current, size);
        LambdaQueryWrapper<SysLog> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.hasText(operName)) {
            wrapper.like(SysLog::getOperName, operName);
        }
        if (StringUtils.hasText(title)) {
            wrapper.like(SysLog::getTitle, title);
        }
        if (businessType != null) {
            wrapper.eq(SysLog::getBusinessType, businessType);
        }
        if (status != null) {
            wrapper.eq(SysLog::getStatus, status);
        }
        if (StringUtils.hasText(startTime)) {
            LocalDateTime start = LocalDateTime.parse(startTime + " 00:00:00",
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            wrapper.ge(SysLog::getOperTime, start);
        }
        if (StringUtils.hasText(endTime)) {
            LocalDateTime end = LocalDateTime.parse(endTime + " 23:59:59",
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            wrapper.le(SysLog::getOperTime, end);
        }

        wrapper.orderByDesc(SysLog::getOperTime);
        return baseMapper.selectPage(page, wrapper);
    }
}
