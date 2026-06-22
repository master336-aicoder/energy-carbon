package com.seatone.system.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.seatone.system.entity.SysDept;
import com.seatone.system.mapper.SysDeptMapper;
import com.seatone.system.service.SysDeptService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SysDeptServiceImpl extends ServiceImpl<SysDeptMapper, SysDept> implements SysDeptService {

    @Override
    public List<SysDept> buildDeptTree() {
        List<SysDept> all = baseMapper.selectDeptTree();
        if (all == null || all.isEmpty()) {
            return new ArrayList<>();
        }
        List<Long> deptIds = all.stream().map(SysDept::getId).collect(Collectors.toList());
        List<SysDept> tree = new ArrayList<>();
        for (SysDept dept : all) {
            if (dept.getParentId() == null || dept.getParentId() == 0L || !deptIds.contains(dept.getParentId())) {
                tree.add(dept);
            }
        }
        tree.sort(Comparator.comparingInt(SysDept::getSort));
        return tree;
    }
}
