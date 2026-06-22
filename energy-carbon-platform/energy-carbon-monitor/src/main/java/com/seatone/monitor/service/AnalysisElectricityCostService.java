package com.seatone.monitor.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.seatone.monitor.entity.AnalysisElectricityCost;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface AnalysisElectricityCostService extends IService<AnalysisElectricityCost> {

    BigDecimal sumTotalCost(String startDate, String endDate, Long meterId);

    Map<String, Object> summaryByPeriod(String startDate, String endDate, Long meterId);

    List<Map<String, Object>> detailByDate(String startDate, String endDate, Long meterId);
}