package com.seatone.monitor.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.seatone.monitor.entity.AnalysisBenchmark;
import com.seatone.monitor.entity.AnalysisEnergyEfficiency;
import com.seatone.monitor.mapper.AnalysisBenchmarkMapper;
import com.seatone.monitor.mapper.AnalysisEnergyEfficiencyMapper;
import com.seatone.monitor.service.AnalysisEnergyEfficiencyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AnalysisEnergyEfficiencyServiceImpl extends ServiceImpl<AnalysisEnergyEfficiencyMapper, AnalysisEnergyEfficiency> implements AnalysisEnergyEfficiencyService {

    @Autowired
    private AnalysisBenchmarkMapper benchmarkMapper;

    @Override
    public List<Map<String, Object>> trendData(Long unitId, String startDate, String endDate) {
        return baseMapper.trendData(unitId, startDate, endDate);
    }

    @Override
    public Map<String, Object> avgIntensity(Long unitId, String startDate, String endDate) {
        return baseMapper.avgIntensity(unitId, startDate, endDate);
    }

    @Override
    public Map<String, Object> calculateIntensity(Long unitId, String startDate, String endDate) {
        Map<String, Object> result = new HashMap<>();
        // 查询总产出和总能耗
        List<AnalysisEnergyEfficiency> list = lambdaQuery()
                .eq(AnalysisEnergyEfficiency::getEnergyUnitId, unitId)
                .ge(startDate != null, AnalysisEnergyEfficiency::getStatDate, startDate)
                .le(endDate != null, AnalysisEnergyEfficiency::getStatDate, endDate)
                .list();

        BigDecimal totalOutput = BigDecimal.ZERO;
        BigDecimal totalEnergy = BigDecimal.ZERO;
        for (AnalysisEnergyEfficiency e : list) {
            totalOutput = totalOutput.add(e.getOutputValue() != null ? e.getOutputValue() : BigDecimal.ZERO);
            totalEnergy = totalEnergy.add(e.getEnergyConsumption() != null ? e.getEnergyConsumption() : BigDecimal.ZERO);
        }

        result.put("totalOutput", totalOutput);
        result.put("totalEnergy", totalEnergy);
        if (totalOutput.compareTo(BigDecimal.ZERO) > 0) {
            result.put("intensity", totalEnergy.divide(totalOutput, 6, BigDecimal.ROUND_HALF_UP));
        } else {
            result.put("intensity", BigDecimal.ZERO);
        }
        return result;
    }

    @Override
    public Map<String, Object> benchmarkAnalysis(Long unitId, String industry, String startDate, String endDate) {
        Map<String, Object> result = new HashMap<>();
        Map<String, Object> intensity = avgIntensity(unitId, startDate, endDate);
        // 防御null：avgIntensity在无数据时可能返回null
        Object avgIntensityVal = (intensity != null) ? intensity.get("avgIntensity") : null;
        result.put("unitIntensity", avgIntensityVal);

        AnalysisBenchmark benchmark = benchmarkMapper.findIndustryBenchmark(industry);
        if (benchmark != null) {
            result.put("benchmarkName", benchmark.getBenchmarkName());
            result.put("benchmarkValue", benchmark.getBenchmarkValue());
            result.put("benchmarkUnit", benchmark.getBenchmarkUnit());
            BigDecimal unitVal = null;
            if (avgIntensityVal instanceof BigDecimal) {
                unitVal = (BigDecimal) avgIntensityVal;
            } else if (avgIntensityVal instanceof Number) {
                unitVal = new BigDecimal(avgIntensityVal.toString());
            }
            BigDecimal benchVal = benchmark.getBenchmarkValue();
            if (unitVal != null && benchVal != null && benchVal.compareTo(BigDecimal.ZERO) > 0) {
                result.put("gapRate", unitVal.subtract(benchVal).divide(benchVal, 4, BigDecimal.ROUND_HALF_UP)
                        .multiply(new BigDecimal("100")).setScale(2, BigDecimal.ROUND_HALF_UP));
                result.put("status", unitVal.compareTo(benchVal) <= 0 ? "达标" : "超标");
            }
        }
        return result;
    }
}