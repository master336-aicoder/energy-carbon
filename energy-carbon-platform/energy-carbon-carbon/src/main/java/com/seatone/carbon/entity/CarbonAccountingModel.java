package com.seatone.carbon.entity;

import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.seatone.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("carbon_accounting_model")
public class CarbonAccountingModel extends BaseEntity {

    private String modelName;

    private Long energyUnitId;

    private String modelConfig;

    private Integer status;

    private String remark;

    @TableLogic
    private Integer deleted;
}