package com.seatone.carbon.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("carbon_accounting_detail")
public class CarbonAccountingDetail {

    private Long id;

    private Long accountingId;

    private String activityName;

    private Long energyTypeId;

    private BigDecimal activityData;

    private String activityUnit;

    private BigDecimal emissionFactor;

    private String factorUnit;

    private BigDecimal emission;

    private String emissionUnit;

    @ApiModelProperty("来源说明")
    private String sourceDescription;

    @ApiModelProperty("排放类别")
    private String category;

    @ApiModelProperty("排放源")
    private String emissionSource;

    @ApiModelProperty("数据来源")
    private String dataSource;

    @ApiModelProperty("运算符")
    private String operator;

    @ApiModelProperty("关联指标")
    private String relatedIndicator;

    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;
}