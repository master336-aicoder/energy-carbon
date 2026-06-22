package com.seatone.system.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.seatone.system.entity.AiServiceRecord;
import com.seatone.system.mapper.AiServiceRecordMapper;
import com.seatone.system.service.AiServiceRecordService;
import org.springframework.stereotype.Service;

@Service
public class AiServiceRecordServiceImpl extends ServiceImpl<AiServiceRecordMapper, AiServiceRecord> implements AiServiceRecordService {
}
