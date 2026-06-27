package com.seatone.system.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.seatone.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("carbon_report")
public class CarbonReport extends BaseEntity {

    private String reportName;
    private Integer reportYear;
    private String reportType;
    private BigDecimal totalEmission;
    private String unit;
    private Integer status;
    private String remark;
}
