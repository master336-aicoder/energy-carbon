package com.seatone.system.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.seatone.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("report_production")
public class ReportProduction extends BaseEntity {

    private String productName;
    private String productCode;
    private BigDecimal planQty;
    private BigDecimal actualQty;
    private String unit;
    private LocalDate productionDate;
    private String workshop;
    private Integer status;
    private String remark;
}
