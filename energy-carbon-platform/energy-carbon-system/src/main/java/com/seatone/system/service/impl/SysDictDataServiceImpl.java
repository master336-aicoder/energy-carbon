package com.seatone.system.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.seatone.system.entity.SysDictData;
import com.seatone.system.mapper.SysDictDataMapper;
import com.seatone.system.service.SysDictDataService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SysDictDataServiceImpl extends ServiceImpl<SysDictDataMapper, SysDictData> implements SysDictDataService {

    @Override
    public List<SysDictData> listByDictType(String dictType) {
        return baseMapper.selectByDictType(dictType);
    }
}
