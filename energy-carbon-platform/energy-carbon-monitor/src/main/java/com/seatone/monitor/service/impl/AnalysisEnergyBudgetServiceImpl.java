package com.seatone.monitor.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.seatone.monitor.entity.AnalysisEnergyBudget;
import com.seatone.monitor.mapper.AnalysisEnergyBudgetMapper;
import com.seatone.monitor.service.AnalysisEnergyBudgetService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;

@Service
public class AnalysisEnergyBudgetServiceImpl extends ServiceImpl<AnalysisEnergyBudgetMapper, AnalysisEnergyBudget> implements AnalysisEnergyBudgetService {

    @Override
    public List<Map<String, Object>> summaryByYear(Long unitId) {
        return baseMapper.summaryByYear(unitId);
    }

    @Override
    public List<AnalysisEnergyBudget> listByYear(Long unitId, Integer year) {
        return baseMapper.listByYear(unitId, year);
    }

    @Override
    public boolean updateCompletionRate(Long id) {
        AnalysisEnergyBudget budget = getById(id);
        if (budget == null || budget.getTargetValue().compareTo(BigDecimal.ZERO) <= 0) {
            return false;
        }
        BigDecimal rate = budget.getCurrentValue()
                .divide(budget.getTargetValue(), 4, RoundingMode.HALF_UP)
                .multiply(new BigDecimal("100"))
                .setScale(2, RoundingMode.HALF_UP);
        budget.setCompletionRate(rate);
        return updateById(budget);
    }

    @Override
    public boolean batchUpdateCompletionRate(Long unitId, Integer year) {
        List<AnalysisEnergyBudget> list = listByYear(unitId, year);
        for (AnalysisEnergyBudget budget : list) {
            if (budget.getTargetValue().compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal rate = budget.getCurrentValue()
                        .divide(budget.getTargetValue(), 4, RoundingMode.HALF_UP)
                        .multiply(new BigDecimal("100"))
                        .setScale(2, RoundingMode.HALF_UP);
                budget.setCompletionRate(rate);
                updateById(budget);
            }
        }
        return true;
    }
}