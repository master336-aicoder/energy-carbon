package com.seatone.carbon.entity;

import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.seatone.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("carbon_emission_factor")
public class CarbonEmissionFactor extends BaseEntity {

    private String factorName;

    private String factorCategory;

    private Long energyTypeId;

    private BigDecimal factorValue;

    private String factorUnit;

    private String source;

    private Integer effectiveYear;

    private Integer defaultFlag;

    private String remark;

    @TableLogic
    private Integer deleted;
}