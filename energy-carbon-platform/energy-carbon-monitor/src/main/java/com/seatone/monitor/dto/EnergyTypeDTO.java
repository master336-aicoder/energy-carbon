package com.seatone.monitor.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class EnergyTypeDTO {

    private Long id;

    private String typeCode;

    private String typeName;

    private String typeCategory;

    private String unit;

    private BigDecimal conversionCoefficient;

    private BigDecimal carbonFactor;

    private Integer status;

    private String remark;
}
