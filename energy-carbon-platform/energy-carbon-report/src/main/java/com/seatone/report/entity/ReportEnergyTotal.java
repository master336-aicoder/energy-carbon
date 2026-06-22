package com.seatone.report.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("report_energy_total")
public class ReportEnergyTotal {

    private Long id;

    private Long energyUnitId;

    private String reportType;

    private LocalDate statDate;

    private BigDecimal totalConsumption;

    private BigDecimal totalCost;

    private String comparisonPeriod;

    private BigDecimal comparisonChangeRate;

    private LocalDateTime createTime;
}