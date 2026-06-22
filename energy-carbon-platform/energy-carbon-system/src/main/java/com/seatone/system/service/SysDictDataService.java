package com.seatone.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.seatone.system.entity.SysDictData;

import java.util.List;

public interface SysDictDataService extends IService<SysDictData> {

    List<SysDictData> listByDictType(String dictType);
}
