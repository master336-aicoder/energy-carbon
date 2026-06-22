package com.seatone.report.entity;

import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.seatone.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("baseline_consumption")
public class BaselineConsumption extends BaseEntity {

    private Long energyUnitId;

    private LocalDate startDate;

    private LocalDate endDate;

    private BigDecimal baselineValue;

    private String baselineType;

    private String remark;

    @TableLogic
    private Integer deleted;
}