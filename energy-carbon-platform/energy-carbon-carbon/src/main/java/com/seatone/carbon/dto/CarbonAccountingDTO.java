package com.seatone.carbon.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class CarbonAccountingDTO {

    private Long id;

    private String accountingName;

    private Integer accountingYear;

    private String accountingPeriod;

    private String accountingPeriodCode;

    private Long energyUnitId;

    private String scopeType;

    private String scopeTypeCode;

    private BigDecimal totalEmission;

    private BigDecimal totalEmissionTco2;

    private String emissionUnit;

    private String status;

    private String verifyStatus;

    private String dataSource;

    private String extInfo;

    private Integer version;

    private String remark;

    private List<CarbonAccountingDetailDTO> details;

    private String createTime;

    private String updateTime;
}