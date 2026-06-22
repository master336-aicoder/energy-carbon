package com.seatone.report.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("report_energy_summary")
public class ReportEnergySummary {

    private Long id;

    private Long energyUnitId;

    private String reportType;

    private LocalDate statDate;

    private Long energyTypeId;

    private BigDecimal consumption;

    private String consumptionUnit;

    private BigDecimal cost;

    private String costUnit;

    private LocalDateTime createTime;
}