package com.seatone.system.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.seatone.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("report_indicator")
public class ReportIndicator extends BaseEntity {

    private String indicatorName;
    private String indicatorCode;
    private String category;
    private String unit;
    private BigDecimal targetValue;
    private BigDecimal actualValue;
    private String period;
    private Integer status;
    private String remark;
}
