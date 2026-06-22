package com.seatone.carbon.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.seatone.carbon.entity.CarbonAccounting;
import com.seatone.carbon.mapper.CarbonAccountingMapper;
import com.seatone.carbon.service.CarbonAccountingService;
import org.springframework.stereotype.Service;

@Service
public class CarbonAccountingServiceImpl extends ServiceImpl<CarbonAccountingMapper, CarbonAccounting> implements CarbonAccountingService {
}