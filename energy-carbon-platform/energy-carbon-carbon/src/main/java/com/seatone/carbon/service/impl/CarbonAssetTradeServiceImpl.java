package com.seatone.carbon.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.seatone.carbon.entity.CarbonAssetTrade;
import com.seatone.carbon.mapper.CarbonAssetTradeMapper;
import com.seatone.carbon.service.CarbonAssetTradeService;
import org.springframework.stereotype.Service;

@Service
public class CarbonAssetTradeServiceImpl extends ServiceImpl<CarbonAssetTradeMapper, CarbonAssetTrade> implements CarbonAssetTradeService {
}
