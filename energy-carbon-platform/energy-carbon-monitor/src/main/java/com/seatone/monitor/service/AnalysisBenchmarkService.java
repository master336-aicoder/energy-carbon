package com.seatone.monitor.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.seatone.monitor.entity.AnalysisBenchmark;

import java.util.List;

public interface AnalysisBenchmarkService extends IService<AnalysisBenchmark> {

    AnalysisBenchmark findIndustryBenchmark(String industry);

    List<AnalysisBenchmark> findAllActive();
}