package com.seatone.carbon.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CarbonEmissionFactorDTO {

    private Long id;

    private String factorName;

    private String factorCategory;

    private Long energyTypeId;

    private BigDecimal factorValue;

    private String factorUnit;

    private String source;

    private Integer effectiveYear;

    private Integer defaultFlag;

    private String remark;
}