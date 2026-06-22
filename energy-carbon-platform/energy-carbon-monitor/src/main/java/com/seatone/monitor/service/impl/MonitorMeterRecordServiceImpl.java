package com.seatone.monitor.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.seatone.monitor.entity.MonitorMeterRecord;
import com.seatone.monitor.mapper.MonitorMeterRecordMapper;
import com.seatone.monitor.service.MonitorMeterRecordService;
import org.springframework.stereotype.Service;

@Service
public class MonitorMeterRecordServiceImpl extends ServiceImpl<MonitorMeterRecordMapper, MonitorMeterRecord> implements MonitorMeterRecordService {
}