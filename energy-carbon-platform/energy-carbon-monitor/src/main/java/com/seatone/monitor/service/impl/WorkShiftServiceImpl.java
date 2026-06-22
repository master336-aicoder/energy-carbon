package com.seatone.monitor.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.seatone.monitor.entity.WorkShift;
import com.seatone.monitor.mapper.WorkShiftMapper;
import com.seatone.monitor.service.WorkShiftService;
import org.springframework.stereotype.Service;

@Service
public class WorkShiftServiceImpl extends ServiceImpl<WorkShiftMapper, WorkShift> implements WorkShiftService {
}
