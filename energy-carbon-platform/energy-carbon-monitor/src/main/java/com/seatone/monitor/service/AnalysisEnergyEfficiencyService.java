package com.seatone.monitor.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.seatone.monitor.entity.AnalysisEnergyEfficiency;

import java.util.List;
import java.util.Map;

public interface AnalysisEnergyEfficiencyService extends IService<AnalysisEnergyEfficiency> {

    List<Map<String, Object>> trendData(Long unitId, String startDate, String endDate);

    Map<String, Object> avgIntensity(Long unitId, String startDate, String endDate);

    /**
     * 根据产出和能耗计算单耗
     */
    Map<String, Object> calculateIntensity(Long unitId, String startDate, String endDate);

    /**
     * 对标分析：本单位 vs 行业基准
     */
    Map<String, Object> benchmarkAnalysis(Long unitId, String industry, String startDate, String endDate);
}