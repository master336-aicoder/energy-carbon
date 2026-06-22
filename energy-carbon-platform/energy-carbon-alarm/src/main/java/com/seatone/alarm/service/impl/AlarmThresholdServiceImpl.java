package com.seatone.alarm.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.seatone.alarm.entity.AlarmThreshold;
import com.seatone.alarm.mapper.AlarmThresholdMapper;
import com.seatone.alarm.service.AlarmThresholdService;
import org.springframework.stereotype.Service;

@Service
public class AlarmThresholdServiceImpl extends ServiceImpl<AlarmThresholdMapper, AlarmThreshold> implements AlarmThresholdService {
}