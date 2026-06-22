package com.seatone.report.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("report_cost_stat")
public class ReportCostStat {

    private Long id;

    private Long energyUnitId;

    private String reportType;

    private LocalDate statDate;

    private Long energyTypeId;

    private BigDecimal cost;

    private String costUnit;

    private LocalDateTime createTime;
}