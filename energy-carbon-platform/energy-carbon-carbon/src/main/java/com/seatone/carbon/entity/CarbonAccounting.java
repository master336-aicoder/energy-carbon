package com.seatone.carbon.entity;

import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.seatone.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("carbon_accounting")
public class CarbonAccounting extends BaseEntity {

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

    @TableLogic
    private Integer deleted;
}