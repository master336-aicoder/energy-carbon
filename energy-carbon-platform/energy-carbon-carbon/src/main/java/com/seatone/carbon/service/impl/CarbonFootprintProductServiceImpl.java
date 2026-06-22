package com.seatone.carbon.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.seatone.carbon.entity.CarbonFootprintProduct;
import com.seatone.carbon.mapper.CarbonFootprintProductMapper;
import com.seatone.carbon.service.CarbonFootprintProductService;
import org.springframework.stereotype.Service;

@Service
public class CarbonFootprintProductServiceImpl extends ServiceImpl<CarbonFootprintProductMapper, CarbonFootprintProduct> implements CarbonFootprintProductService {
}