package com.seatone.alarm.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.seatone.alarm.entity.AlarmRecord;
import com.seatone.alarm.mapper.AlarmRecordMapper;
import com.seatone.alarm.service.AlarmRecordService;
import org.springframework.stereotype.Service;

@Service
public class AlarmRecordServiceImpl extends ServiceImpl<AlarmRecordMapper, AlarmRecord> implements AlarmRecordService {
}