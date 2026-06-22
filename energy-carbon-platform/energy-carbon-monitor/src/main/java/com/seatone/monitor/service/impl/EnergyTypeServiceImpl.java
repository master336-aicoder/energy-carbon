package com.seatone.monitor.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.seatone.monitor.entity.EnergyType;
import com.seatone.monitor.mapper.EnergyTypeMapper;
import com.seatone.monitor.service.EnergyTypeService;
import org.springframework.stereotype.Service;

@Service
public class EnergyTypeServiceImpl extends ServiceImpl<EnergyTypeMapper, EnergyType> implements EnergyTypeService {
}
