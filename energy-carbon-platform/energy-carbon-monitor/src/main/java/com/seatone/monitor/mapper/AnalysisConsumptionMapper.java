package com.seatone.monitor.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.seatone.monitor.entity.AnalysisConsumption;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Mapper
public interface AnalysisConsumptionMapper extends BaseMapper<AnalysisConsumption> {

    @Select("<script>" +
            "SELECT COALESCE(SUM(consumption), 0) FROM analysis_energy_consumption " +
            "WHERE energy_unit_id = #{unitId} AND stat_period = #{period} " +
            "<if test='startDate != null'> AND stat_date &gt;= #{startDate} </if>" +
            "<if test='endDate != null'> AND stat_date &lt;= #{endDate} </if>" +
            "<if test='energyTypeId != null'> AND energy_type_id = #{energyTypeId} </if>" +
            "</script>")
    BigDecimal sumConsumption(@Param("unitId") Long unitId,
                              @Param("period") String period,
                              @Param("startDate") String startDate,
                              @Param("endDate") String endDate,
                              @Param("energyTypeId") Long energyTypeId);

    @Select("<script>" +
            "SELECT energy_type_id as energyTypeId, SUM(consumption) as total " +
            "FROM analysis_energy_consumption " +
            "WHERE energy_unit_id = #{unitId} AND stat_period = #{period} " +
            "<if test='startDate != null'> AND stat_date &gt;= #{startDate} </if>" +
            "<if test='endDate != null'> AND stat_date &lt;= #{endDate} </if>" +
            "GROUP BY energy_type_id" +
            "</script>")
    List<Map<String, Object>> groupByEnergyType(@Param("unitId") Long unitId,
                                                @Param("period") String period,
                                                @Param("startDate") String startDate,
                                                @Param("endDate") String endDate);

    @Select("<script>" +
            "SELECT stat_date as statDate, SUM(consumption) as total " +
            "FROM analysis_energy_consumption " +
            "WHERE energy_unit_id = #{unitId} AND stat_period = #{period} " +
            "<if test='startDate != null'> AND stat_date &gt;= #{startDate} </if>" +
            "<if test='endDate != null'> AND stat_date &lt;= #{endDate} </if>" +
            "<if test='energyTypeId != null'> AND energy_type_id = #{energyTypeId} </if>" +
            "GROUP BY stat_date ORDER BY stat_date" +
            "</script>")
    List<Map<String, Object>> trendByDate(@Param("unitId") Long unitId,
                                          @Param("period") String period,
                                          @Param("startDate") String startDate,
                                          @Param("endDate") String endDate,
                                          @Param("energyTypeId") Long energyTypeId);
}