package com.seatone.monitor.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.seatone.monitor.entity.AnalysisElectricityCost;
import com.seatone.monitor.mapper.AnalysisElectricityCostMapper;
import com.seatone.monitor.service.AnalysisElectricityCostService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Service
public class AnalysisElectricityCostServiceImpl extends ServiceImpl<AnalysisElectricityCostMapper, AnalysisElectricityCost> implements AnalysisElectricityCostService {

    @Override
    public BigDecimal sumTotalCost(String startDate, String endDate, Long meterId) {
        return baseMapper.sumTotalCost(startDate, endDate, meterId);
    }

    @Override
    public Map<String, Object> summaryByPeriod(String startDate, String endDate, Long meterId) {
        return baseMapper.summaryByPeriod(startDate, endDate, meterId);
    }

    @Override
    public List<Map<String, Object>> detailByDate(String startDate, String endDate, Long meterId) {
        return baseMapper.detailByDate(startDate, endDate, meterId);
    }
}