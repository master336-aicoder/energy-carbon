package com.seatone.system.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.seatone.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("report_baseline")
public class ReportBaseline extends BaseEntity {

    private String baselineName;
    private Integer baselineYear;
    private String energyType;
    private BigDecimal baselineValue;
    private String unit;
    private Integer status;
    private String remark;
}
