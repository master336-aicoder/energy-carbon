package com.seatone.monitor.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.seatone.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("energy_type")
public class EnergyType extends BaseEntity {

    private String typeCode;

    private String typeName;

    private String typeCategory;

    private String unit;

    private BigDecimal conversionCoefficient;

    private BigDecimal carbonFactor;

    private Integer status;

    private String remark;
}
