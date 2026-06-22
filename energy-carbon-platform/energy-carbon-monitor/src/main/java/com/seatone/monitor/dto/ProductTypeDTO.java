package com.seatone.monitor.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductTypeDTO {

    private Long id;

    private String productCode;

    private String productName;

    private String productCategory;

    private String unit;

    private BigDecimal carbonFactor;

    private Integer status;

    private String remark;
}
