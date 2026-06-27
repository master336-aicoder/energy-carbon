package com.seatone.system.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.seatone.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("efficiency_benchmark")
public class EfficiencyBenchmark extends BaseEntity {

    private String benchmarkName;
    private String industry;
    private String unitName;
    private BigDecimal benchmarkValue;
    private String unit;
    private BigDecimal ourValue;
    private BigDecimal gap;
    private Integer status;
    private String remark;
}
