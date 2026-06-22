package com.seatone.monitor.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.seatone.monitor.entity.EnergyMeter;
import com.seatone.monitor.mapper.EnergyMeterMapper;
import com.seatone.monitor.service.EnergyMeterService;
import org.springframework.stereotype.Service;

@Service
public class EnergyMeterServiceImpl extends ServiceImpl<EnergyMeterMapper, EnergyMeter> implements EnergyMeterService {
}
