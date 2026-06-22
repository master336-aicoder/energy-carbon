package com.seatone.carbon.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.seatone.carbon.entity.CarbonAccountingModel;
import com.seatone.carbon.mapper.CarbonAccountingModelMapper;
import com.seatone.carbon.service.CarbonAccountingModelService;
import org.springframework.stereotype.Service;

@Service
public class CarbonAccountingModelServiceImpl extends ServiceImpl<CarbonAccountingModelMapper, CarbonAccountingModel> implements CarbonAccountingModelService {
}