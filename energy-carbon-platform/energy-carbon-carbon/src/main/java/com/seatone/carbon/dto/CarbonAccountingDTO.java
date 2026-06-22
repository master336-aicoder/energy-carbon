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

    private Long energyUnitId;

    private String scopeType;

    private BigDecimal totalEmission;

    private String emissionUnit;

    private String status;

    private String remark;

    private List<CarbonAccountingDetailDTO> details;

    private String createTime;

    private String updateTime;
}