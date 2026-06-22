package com.seatone.report.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.seatone.report.entity.ReportEnergySummary;
import com.seatone.report.mapper.ReportEnergySummaryMapper;
import com.seatone.report.service.ReportEnergySummaryService;
import org.springframework.stereotype.Service;

@Service
public class ReportEnergySummaryServiceImpl extends ServiceImpl<ReportEnergySummaryMapper, ReportEnergySummary> implements ReportEnergySummaryService {
}