package com.seatone.system.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.seatone.common.annotation.CacheEvictCustom;
import com.seatone.common.annotation.CacheableCustom;
import com.seatone.system.entity.SysDictData;
import com.seatone.system.mapper.SysDictDataMapper;
import com.seatone.system.service.SysDictDataService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SysDictDataServiceImpl extends ServiceImpl<SysDictDataMapper, SysDictData> implements SysDictDataService {

    @Override
    @CacheableCustom(prefix = "dict:type", key = "#dictType", ttl = 30, description = "字典数据按类型缓存")
    public List<SysDictData> listByDictType(String dictType) {
        return baseMapper.selectByDictType(dictType);
    }
}
