package com.seatone.monitor.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class AnalysisBenchmarkDTO {
    private Long id;
    private String benchmarkName;
    private String industry;
    private Long energyTypeId;
    private String energyTypeName;
    private String energyCategory;
    private BigDecimal benchmarkValue;
    private String benchmarkUnit;
    private String source;
    private LocalDate effectiveDate;
    private LocalDate expireDate;
    private String remark;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}