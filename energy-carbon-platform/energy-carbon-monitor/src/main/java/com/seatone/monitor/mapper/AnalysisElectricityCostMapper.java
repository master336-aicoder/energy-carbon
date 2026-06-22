package com.seatone.monitor.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.seatone.monitor.entity.AnalysisElectricityCost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Mapper
public interface AnalysisElectricityCostMapper extends BaseMapper<AnalysisElectricityCost> {

    @Select("<script>" +
            "SELECT COALESCE(SUM(total_cost), 0) FROM analysis_electricity_cost " +
            "WHERE 1=1 " +
            "<if test='startDate != null'> AND stat_date &gt;= #{startDate} </if>" +
            "<if test='endDate != null'> AND stat_date &lt;= #{endDate} </if>" +
            "<if test='meterId != null'> AND meter_id = #{meterId} </if>" +
            "</script>")
    BigDecimal sumTotalCost(@Param("startDate") String startDate,
                            @Param("endDate") String endDate,
                            @Param("meterId") Long meterId);

    @Select("<script>" +
            "SELECT COALESCE(SUM(peak_power), 0) as peakPower, COALESCE(SUM(valley_power), 0) as valleyPower, " +
            "COALESCE(SUM(flat_power), 0) as flatPower, COALESCE(SUM(total_cost), 0) as totalCost " +
            "FROM analysis_electricity_cost " +
            "WHERE 1=1 " +
            "<if test='startDate != null'> AND stat_date &gt;= #{startDate} </if>" +
            "<if test='endDate != null'> AND stat_date &lt;= #{endDate} </if>" +
            "<if test='meterId != null'> AND meter_id = #{meterId} </if>" +
            "</script>")
    Map<String, Object> summaryByPeriod(@Param("startDate") String startDate,
                                        @Param("endDate") String endDate,
                                        @Param("meterId") Long meterId);

    @Select("<script>" +
            "SELECT stat_date as statDate, SUM(peak_power) as peakPower, SUM(valley_power) as valleyPower, " +
            "SUM(flat_power) as flatPower, SUM(peak_cost) as peakCost, SUM(valley_cost) as valleyCost, " +
            "SUM(flat_cost) as flatCost, SUM(total_cost) as totalCost " +
            "FROM analysis_electricity_cost " +
            "WHERE 1=1 " +
            "<if test='startDate != null'> AND stat_date &gt;= #{startDate} </if>" +
            "<if test='endDate != null'> AND stat_date &lt;= #{endDate} </if>" +
            "<if test='meterId != null'> AND meter_id = #{meterId} </if>" +
            "GROUP BY stat_date ORDER BY stat_date" +
            "</script>")
    List<Map<String, Object>> detailByDate(@Param("startDate") String startDate,
                                           @Param("endDate") String endDate,
                                           @Param("meterId") Long meterId);
}