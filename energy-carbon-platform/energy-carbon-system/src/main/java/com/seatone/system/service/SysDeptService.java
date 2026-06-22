package com.seatone.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.seatone.system.entity.SysDept;

import java.util.List;

public interface SysDeptService extends IService<SysDept> {

    List<SysDept> buildDeptTree();
}
