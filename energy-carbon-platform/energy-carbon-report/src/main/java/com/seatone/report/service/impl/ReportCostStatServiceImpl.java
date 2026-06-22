package com.seatone.report.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.seatone.report.entity.ReportCostStat;
import com.seatone.report.mapper.ReportCostStatMapper;
import com.seatone.report.service.ReportCostStatService;
import org.springframework.stereotype.Service;

@Service
public class ReportCostStatServiceImpl extends ServiceImpl<ReportCostStatMapper, ReportCostStat> implements ReportCostStatService {
}