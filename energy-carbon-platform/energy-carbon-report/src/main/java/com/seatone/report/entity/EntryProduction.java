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
@TableName("entry_production")
public class EntryProduction extends BaseEntity {

    private Long energyUnitId;

    private Long productTypeId;

    private LocalDate productionDate;

    private BigDecimal outputQuantity;

    private String outputUnit;

    private String batchNumber;

    private String remark;

    private String entryPerson;

    @TableLogic
    private Integer deleted;
}