package com.seatone.monitor.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class AnalysisEnergyEfficiencyDTO {
    private Long id;
    private Long energyUnitId;
    private String energyUnitName;
    private LocalDate statDate;
    private BigDecimal outputValue;
    private String outputUnit;
    private BigDecimal energyConsumption;
    private String energyUnit;
    private BigDecimal intensity;
    private BigDecimal benchmarkValue;
    private String benchmarkUnit;
    private LocalDate createTime;
}