package com.seatone.system.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.seatone.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("efficiency_analysis")
public class EfficiencyAnalysis extends BaseEntity {

    private String analysisName;
    private String deviceName;
    private BigDecimal efficiencyValue;
    private String unit;
    private BigDecimal standardValue;
    private Integer status;
    private String remark;
}
