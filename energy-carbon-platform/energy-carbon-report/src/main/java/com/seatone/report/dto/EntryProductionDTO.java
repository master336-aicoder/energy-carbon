package com.seatone.report.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class EntryProductionDTO {
    private Long id;
    private Long energyUnitId;
    private Long productTypeId;
    private LocalDate productionDate;
    private BigDecimal outputQuantity;
    private String outputUnit;
    private String batchNumber;
    private String remark;
    private String entryPerson;
}