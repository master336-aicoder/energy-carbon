package com.seatone.carbon.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.seatone.carbon.entity.CarbonMarketInfo;
import com.seatone.carbon.mapper.CarbonMarketInfoMapper;
import com.seatone.carbon.service.CarbonMarketInfoService;
import org.springframework.stereotype.Service;

@Service
public class CarbonMarketInfoServiceImpl extends ServiceImpl<CarbonMarketInfoMapper, CarbonMarketInfo> implements CarbonMarketInfoService {
}
