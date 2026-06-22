package com.seatone.carbon.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.seatone.carbon.entity.CarbonAsset;
import com.seatone.carbon.mapper.CarbonAssetMapper;
import com.seatone.carbon.service.CarbonAssetService;
import org.springframework.stereotype.Service;

@Service
public class CarbonAssetServiceImpl extends ServiceImpl<CarbonAssetMapper, CarbonAsset> implements CarbonAssetService {
}
