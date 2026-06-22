package com.seatone.monitor.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.seatone.monitor.entity.AnalysisBenchmark;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface AnalysisBenchmarkMapper extends BaseMapper<AnalysisBenchmark> {

    @Select("SELECT * FROM analysis_benchmark WHERE industry = #{industry} AND deleted = 0 " +
            "AND (effective_date IS NULL OR effective_date <= CURDATE()) " +
            "AND (expire_date IS NULL OR expire_date >= CURDATE()) " +
            "ORDER BY benchmark_value ASC LIMIT 1")
    AnalysisBenchmark findIndustryBenchmark(@Param("industry") String industry);

    @Select("SELECT * FROM analysis_benchmark WHERE deleted = 0 ORDER BY create_time DESC")
    List<AnalysisBenchmark> findAllActive();
}