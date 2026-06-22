package com.seatone.carbon.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CarbonAccountingDetailDTO {

    private Long id;

    private Long accountingId;

    private String activityName;

    private Long energyTypeId;

    private BigDecimal activityData;

    private String activityUnit;

    private BigDecimal emissionFactor;

    private String factorUnit;

    private BigDecimal emission;

    private String emissionUnit;

    private String sourceDescription;
}