package com.seatone.monitor.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.seatone.monitor.entity.IotGateway;
import com.seatone.monitor.mapper.IotGatewayMapper;
import com.seatone.monitor.service.IotGatewayService;
import org.springframework.stereotype.Service;

@Service
public class IotGatewayServiceImpl extends ServiceImpl<IotGatewayMapper, IotGateway> implements IotGatewayService {
}
