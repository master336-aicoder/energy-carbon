package com.seatone.monitor.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class AnalysisElectricityCostDTO {
    private Long id;
    private Long meterId;
    private String meterName;
    private LocalDate statDate;
    private BigDecimal peakPower;
    private BigDecimal valleyPower;
    private BigDecimal flatPower;
    private BigDecimal peakCost;
    private BigDecimal valleyCost;
    private BigDecimal flatCost;
    private BigDecimal totalCost;
    private LocalDate createTime;
}