package com.seatone.monitor.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class AnalysisEnergyBudgetDTO {
    private Long id;
    private Long energyUnitId;
    private String energyUnitName;
    private Integer budgetYear;
    private String budgetType;
    private String budgetTypeName;
    private BigDecimal targetValue;
    private String targetUnit;
    private BigDecimal currentValue;
    private BigDecimal completionRate;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}