package com.seatone.monitor.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class PeakShiftingDTO {

    private Long id;

    private String planName;

    private LocalDate planDate;

    private String shiftType;

    private String productionLine;

    private BigDecimal targetOutput;

    private BigDecimal actualOutput;

    private BigDecimal energyConsumption;

    private BigDecimal energyCost;

    private BigDecimal savingRate;

    private Integer status;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
