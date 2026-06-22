package com.seatone.monitor.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.seatone.monitor.entity.MonitorStatusLog;
import com.seatone.monitor.mapper.MonitorStatusLogMapper;
import com.seatone.monitor.service.MonitorStatusLogService;
import org.springframework.stereotype.Service;

@Service
public class MonitorStatusLogServiceImpl extends ServiceImpl<MonitorStatusLogMapper, MonitorStatusLog> implements MonitorStatusLogService {
}