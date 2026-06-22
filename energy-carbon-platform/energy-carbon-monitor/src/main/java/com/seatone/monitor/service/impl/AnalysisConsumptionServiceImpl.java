package com.seatone.monitor.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.seatone.monitor.entity.AnalysisConsumption;
import com.seatone.monitor.mapper.AnalysisConsumptionMapper;
import com.seatone.monitor.service.AnalysisConsumptionService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

@Service
public class AnalysisConsumptionServiceImpl extends ServiceImpl<AnalysisConsumptionMapper, AnalysisConsumption> implements AnalysisConsumptionService {

    @Override
    public BigDecimal sumConsumption(Long unitId, String period, String startDate, String endDate, Long energyTypeId) {
        return baseMapper.sumConsumption(unitId, period, startDate, endDate, energyTypeId);
    }

    @Override
    public List<Map<String, Object>> groupByEnergyType(Long unitId, String period, String startDate, String endDate) {
        return baseMapper.groupByEnergyType(unitId, period, startDate, endDate);
    }

    @Override
    public List<Map<String, Object>> trendByDate(Long unitId, String period, String startDate, String endDate, Long energyTypeId) {
        return baseMapper.trendByDate(unitId, period, startDate, endDate, energyTypeId);
    }

    @Override
    public BigDecimal getSamePeriodConsumption(Long unitId, String startDate, String endDate, Long energyTypeId) {
        if (startDate == null || endDate == null) {
            return BigDecimal.ZERO;
        }
        LocalDate start = LocalDate.parse(startDate);
        LocalDate end = LocalDate.parse(endDate);
        long days = ChronoUnit.DAYS.between(start, end);
        LocalDate prevStart = start.minusDays(days + 1);
        LocalDate prevEnd = start.minusDays(1);
        return baseMapper.sumConsumption(unitId, "DAY", prevStart.toString(), prevEnd.toString(), energyTypeId);
    }

    @Override
    public BigDecimal getPreviousPeriodConsumption(Long unitId, String period, String endDate, Long energyTypeId) {
        if (endDate == null) return BigDecimal.ZERO;
        LocalDate end = LocalDate.parse(endDate);
        LocalDate start;
        switch (period.toUpperCase()) {
            case "MONTH":
                start = end.minusMonths(1).plusDays(1);
                end = end.minusMonths(1).withDayOfMonth(end.minusMonths(1).lengthOfMonth());
                break;
            case "YEAR":
                start = end.minusYears(1).plusDays(1);
                end = end.minusYears(1).withDayOfYear(end.minusYears(1).lengthOfYear());
                break;
            default:
                start = end.minusDays(1);
                end = end.minusDays(1);
        }
        return baseMapper.sumConsumption(unitId, period, start.toString(), end.toString(), energyTypeId);
    }
}