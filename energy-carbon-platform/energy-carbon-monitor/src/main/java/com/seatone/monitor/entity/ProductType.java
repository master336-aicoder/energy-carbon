package com.seatone.monitor.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.seatone.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("product_type")
public class ProductType extends BaseEntity {

    private String productCode;

    private String productName;

    private String productCategory;

    private String unit;

    private BigDecimal carbonFactor;

    private Integer status;

    private String remark;
}
