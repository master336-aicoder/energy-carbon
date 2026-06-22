package com.seatone.monitor.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.seatone.monitor.entity.AnalysisEnergyEfficiency;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface AnalysisEnergyEfficiencyMapper extends BaseMapper<AnalysisEnergyEfficiency> {

    @Select("<script>" +
            "SELECT stat_date as statDate, output_value as outputValue, energy_consumption as energyConsumption, " +
            "intensity, benchmark_value as benchmarkValue " +
            "FROM analysis_energy_efficiency " +
            "WHERE energy_unit_id = #{unitId} " +
            "<if test='startDate != null'> AND stat_date &gt;= #{startDate} </if>" +
            "<if test='endDate != null'> AND stat_date &lt;= #{endDate} </if>" +
            "ORDER BY stat_date" +
            "</script>")
    List<Map<String, Object>> trendData(@Param("unitId") Long unitId,
                                        @Param("startDate") String startDate,
                                        @Param("endDate") String endDate);

    @Select("<script>" +
            "SELECT energy_unit_id as energyUnitId, AVG(intensity) as avgIntensity " +
            "FROM analysis_energy_efficiency " +
            "WHERE energy_unit_id = #{unitId} " +
            "<if test='startDate != null'> AND stat_date &gt;= #{startDate} </if>" +
            "<if test='endDate != null'> AND stat_date &lt;= #{endDate} </if>" +
            "GROUP BY energy_unit_id" +
            "</script>")
    Map<String, Object> avgIntensity(@Param("unitId") Long unitId,
                                     @Param("startDate") String startDate,
                                     @Param("endDate") String endDate);
}