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

    private String categoryCode;

    private String scopeType;

    private String scopeTypeCode;

    private String emissionSource;

    private String dataSource;

    private String dataQuality;

    private String verifyStatus;

    private String operator;

    private String relatedIndicator;

    private String attachmentUrl;

    private String calcFormula;

    private BigDecimal originalValue;

    private BigDecimal emissionTco2;

    private Integer version;

    private LocalDateTime createTime;
}
