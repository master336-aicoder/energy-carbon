package com.seatone.report.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class EntryIndicatorDTO {
    private Long id;
    private Long energyUnitId;
    private String indicatorName;
    private String indicatorType;
    private BigDecimal indicatorValue;
    private String indicatorUnit;
    private LocalDate entryDate;
    private String entryPerson;
    private String remark;
}