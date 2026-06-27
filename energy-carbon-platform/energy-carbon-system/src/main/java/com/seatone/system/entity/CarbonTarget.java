package com.seatone.system.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.seatone.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("carbon_target")
public class CarbonTarget extends BaseEntity {

    private String targetName;
    private Integer targetYear;
    private Integer targetType;
    private BigDecimal targetValue;
    private BigDecimal actualValue;
    private String unit;
    private Integer status;
    private String remark;
}
