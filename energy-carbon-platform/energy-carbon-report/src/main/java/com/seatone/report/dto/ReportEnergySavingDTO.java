package com.seatone.report.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class ReportEnergySavingDTO {
    private Long id;
    private Long energyUnitId;
    private String reportType;
    private LocalDate statDate;
    private BigDecimal baselineConsumption;
    private BigDecimal actualConsumption;
    private BigDecimal savingAmount;
    private BigDecimal savingRate;
    private String savingAmountUnit;
    private String createTime;
}