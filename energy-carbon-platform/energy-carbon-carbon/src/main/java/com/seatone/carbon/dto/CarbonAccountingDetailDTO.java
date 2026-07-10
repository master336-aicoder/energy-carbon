package com.seatone.carbon.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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

    private String category;

    private String emissionSource;

    private String dataSource;

    private String operator;

    private String relatedIndicator;

    private LocalDateTime createTime;
}
