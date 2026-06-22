package com.seatone.carbon.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.seatone.carbon.entity.CarbonEmissionFactor;
import com.seatone.carbon.mapper.CarbonEmissionFactorMapper;
import com.seatone.carbon.service.CarbonEmissionFactorService;
import org.springframework.stereotype.Service;

@Service
public class CarbonEmissionFactorServiceImpl extends ServiceImpl<CarbonEmissionFactorMapper, CarbonEmissionFactor> implements CarbonEmissionFactorService {
}