package com.seatone.monitor.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.seatone.monitor.entity.EnergyDevice;
import com.seatone.monitor.mapper.EnergyDeviceMapper;
import com.seatone.monitor.service.EnergyDeviceService;
import org.springframework.stereotype.Service;

@Service
public class EnergyDeviceServiceImpl extends ServiceImpl<EnergyDeviceMapper, EnergyDevice> implements EnergyDeviceService {
}
