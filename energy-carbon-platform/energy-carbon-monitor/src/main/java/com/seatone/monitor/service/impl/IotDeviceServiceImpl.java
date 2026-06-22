package com.seatone.monitor.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.seatone.monitor.entity.IotDevice;
import com.seatone.monitor.mapper.IotDeviceMapper;
import com.seatone.monitor.service.IotDeviceService;
import org.springframework.stereotype.Service;

@Service
public class IotDeviceServiceImpl extends ServiceImpl<IotDeviceMapper, IotDevice> implements IotDeviceService {
}
