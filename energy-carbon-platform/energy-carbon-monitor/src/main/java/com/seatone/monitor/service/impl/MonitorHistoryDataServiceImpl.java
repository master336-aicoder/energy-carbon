package com.seatone.monitor.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.seatone.monitor.entity.MonitorHistoryData;
import com.seatone.monitor.mapper.MonitorHistoryDataMapper;
import com.seatone.monitor.service.MonitorHistoryDataService;
import org.springframework.stereotype.Service;

@Service
public class MonitorHistoryDataServiceImpl extends ServiceImpl<MonitorHistoryDataMapper, MonitorHistoryData> implements MonitorHistoryDataService {
}