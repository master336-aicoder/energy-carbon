package com.seatone.monitor.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.seatone.monitor.entity.EnergyTypeTemplate;
import com.seatone.monitor.mapper.EnergyTypeTemplateMapper;
import com.seatone.monitor.service.EnergyTypeTemplateService;
import org.springframework.stereotype.Service;

@Service
public class EnergyTypeTemplateServiceImpl extends ServiceImpl<EnergyTypeTemplateMapper, EnergyTypeTemplate> implements EnergyTypeTemplateService {
}
