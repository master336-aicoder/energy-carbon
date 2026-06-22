package com.seatone.monitor.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class AnalysisConsumptionDTO {
    private Long id;
    private Long energyUnitId;
    private String energyUnitName;
    private Long meterId;
    private String meterName;
    private Long energyTypeId;
    private String energyTypeName;
    private LocalDate statDate;
    private String statPeriod;
    private BigDecimal consumption;
    private String unit;
    private LocalDate createTime;
}