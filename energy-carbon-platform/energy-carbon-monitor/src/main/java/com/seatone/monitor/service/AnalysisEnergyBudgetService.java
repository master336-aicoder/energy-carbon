package com.seatone.monitor.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.seatone.monitor.entity.AnalysisEnergyBudget;

import java.util.List;
import java.util.Map;

public interface AnalysisEnergyBudgetService extends IService<AnalysisEnergyBudget> {

    List<Map<String, Object>> summaryByYear(Long unitId);

    List<AnalysisEnergyBudget> listByYear(Long unitId, Integer year);

    boolean updateCompletionRate(Long id);

    boolean batchUpdateCompletionRate(Long unitId, Integer year);
}