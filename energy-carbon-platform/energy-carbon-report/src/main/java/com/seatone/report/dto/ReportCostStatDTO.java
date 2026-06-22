package com.seatone.report.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class ReportCostStatDTO {
    private Long id;
    private Long energyUnitId;
    private String reportType;
    private LocalDate statDate;
    private Long energyTypeId;
    private BigDecimal cost;
    private String costUnit;
    private String createTime;
}