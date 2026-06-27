package com.seatone.system.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.seatone.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("carbon_target")
public class CarbonTarget extends BaseEntity {

    @NotBlank(message = "目标名称不能为空")
    private String targetName;

    @NotNull(message = "目标年份不能为空")
    private Integer targetYear;

    @NotNull(message = "目标类型不能为空")
    private Integer targetType;

    @NotNull(message = "目标值不能为空")
    private BigDecimal targetValue;

    private BigDecimal actualValue;

    @NotBlank(message = "单位不能为空")
    private String unit;

    private Integer status;

    private String remark;
}
