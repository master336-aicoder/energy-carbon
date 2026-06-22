package com.seatone.report.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class ReportEnergyTotalDTO {
    private Long id;
    private Long energyUnitId;
    private String reportType;
    private LocalDate statDate;
    private BigDecimal totalConsumption;
    private BigDecimal totalCost;
    private String comparisonPeriod;
    private BigDecimal comparisonChangeRate;
    private String createTime;
}