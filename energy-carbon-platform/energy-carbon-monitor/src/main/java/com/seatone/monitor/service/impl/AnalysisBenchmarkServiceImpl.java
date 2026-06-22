package com.seatone.monitor.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.seatone.monitor.entity.AnalysisBenchmark;
import com.seatone.monitor.mapper.AnalysisBenchmarkMapper;
import com.seatone.monitor.service.AnalysisBenchmarkService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AnalysisBenchmarkServiceImpl extends ServiceImpl<AnalysisBenchmarkMapper, AnalysisBenchmark> implements AnalysisBenchmarkService {

    @Override
    public AnalysisBenchmark findIndustryBenchmark(String industry) {
        return baseMapper.findIndustryBenchmark(industry);
    }

    @Override
    public List<AnalysisBenchmark> findAllActive() {
        return baseMapper.findAllActive();
    }
}