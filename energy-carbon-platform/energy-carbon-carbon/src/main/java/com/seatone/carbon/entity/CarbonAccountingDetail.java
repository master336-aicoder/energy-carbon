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

    @ApiModelProperty("排放类别编码")
    private String categoryCode;

    @ApiModelProperty("范围类型")
    private String scopeType;

    @ApiModelProperty("范围类型编码")
    private String scopeTypeCode;

    @ApiModelProperty("排放源")
    private String emissionSource;

    @ApiModelProperty("数据来源")
    private String dataSource;

    @ApiModelProperty("数据质量")
    private String dataQuality;

    @ApiModelProperty("校验状态")
    private String verifyStatus;

    @ApiModelProperty("运算符")
    private String operator;

    @ApiModelProperty("关联指标")
    private String relatedIndicator;

    @ApiModelProperty("凭证附件URL")
    private String attachmentUrl;

    @ApiModelProperty("计算公式")
    private String calcFormula;

    @ApiModelProperty("原始活动数据")
    private BigDecimal originalValue;

    @ApiModelProperty("排放量（吨CO2e）")
    private BigDecimal emissionTco2;

    @ApiModelProperty("版本号")
    private Integer version;

    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;
}