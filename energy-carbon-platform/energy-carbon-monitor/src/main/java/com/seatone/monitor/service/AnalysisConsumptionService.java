package com.seatone.monitor.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.seatone.monitor.entity.AnalysisConsumption;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface AnalysisConsumptionService extends IService<AnalysisConsumption> {

    /**
     * 汇总消耗量
     */
    BigDecimal sumConsumption(Long unitId, String period, String startDate, String endDate, Long energyTypeId);

    /**
     * 按能源品种分组统计
     */
    List<Map<String, Object>> groupByEnergyType(Long unitId, String period, String startDate, String endDate);

    /**
     * 按日期统计趋势
     */
    List<Map<String, Object>> trendByDate(Long unitId, String period, String startDate, String endDate, Long energyTypeId);

    /**
     * 获取同比数据（同期）
     */
    BigDecimal getSamePeriodConsumption(Long unitId, String startDate, String endDate, Long energyTypeId);

    /**
     * 获取环比数据（上一周期）
     */
    BigDecimal getPreviousPeriodConsumption(Long unitId, String period, String endDate, Long energyTypeId);
}