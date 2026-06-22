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
@TableName("entry_indicator")
public class EntryIndicator extends BaseEntity {

    private Long energyUnitId;

    private String indicatorName;

    private String indicatorType;

    private BigDecimal indicatorValue;

    private String indicatorUnit;

    private LocalDate entryDate;

    private String entryPerson;

    private String remark;

    @TableLogic
    private Integer deleted;
}