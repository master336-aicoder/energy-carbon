package com.seatone.monitor.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.seatone.monitor.entity.AnalysisEnergyBudget;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface AnalysisEnergyBudgetMapper extends BaseMapper<AnalysisEnergyBudget> {

    @Select("SELECT budget_year as budgetYear, budget_type as budgetType, " +
            "SUM(target_value) as targetValue, SUM(current_value) as currentValue, " +
            "CASE WHEN SUM(target_value) > 0 THEN ROUND(SUM(current_value) / SUM(target_value) * 100, 2) ELSE 0 END as completionRate " +
            "FROM analysis_energy_budget " +
            "WHERE energy_unit_id = #{unitId} AND deleted = 0 " +
            "GROUP BY budget_year, budget_type ORDER BY budget_year")
    List<Map<String, Object>> summaryByYear(@Param("unitId") Long unitId);

    @Select("SELECT * FROM analysis_energy_budget " +
            "WHERE energy_unit_id = #{unitId} AND budget_year = #{year} AND deleted = 0")
    List<AnalysisEnergyBudget> listByYear(@Param("unitId") Long unitId, @Param("year") Integer year);
}