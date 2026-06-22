package com.seatone.report.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class BaselineConsumptionDTO {
    private Long id;
    private Long energyUnitId;
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal baselineValue;
    private String baselineType;
    private String remark;
}