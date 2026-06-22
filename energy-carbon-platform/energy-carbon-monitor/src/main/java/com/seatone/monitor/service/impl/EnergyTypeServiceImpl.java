package com.seatone.monitor.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.seatone.common.annotation.CacheableCustom;
import com.seatone.monitor.entity.EnergyType;
import com.seatone.monitor.mapper.EnergyTypeMapper;
import com.seatone.monitor.service.EnergyTypeService;
import org.springframework.stereotype.Service;

@Service
public class EnergyTypeServiceImpl extends ServiceImpl<EnergyTypeMapper, EnergyType> implements EnergyTypeService {

    /**
     * 获取所有能源品种（带缓存）
     */
    @Override
    @CacheableCustom(prefix = "energy:type", key = "'all'", ttl = 60, description = "能源品种列表缓存")
    public java.util.List<EnergyType> list() {
        return super.list();
    }
}
