package com.seatone.carbon.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 排放报告数据
 */
@Data
public class EmissionReportDTO {

    private Long accountingId;

    private String accountingName;

    private Integer accountingYear;

    private String accountingPeriod;

    /** 范围一排放 */
    private BigDecimal scope1Emission;

    /** 范围二排放 */
    private BigDecimal scope2Emission;

    /** 范围三排放 */
    private BigDecimal scope3Emission;

    /** 总排放量 */
    private BigDecimal totalEmission;

    private String emissionUnit;

    /** 各范围占比 */
    private BigDecimal scope1Percent;

    private BigDecimal scope2Percent;

    private BigDecimal scope3Percent;
}