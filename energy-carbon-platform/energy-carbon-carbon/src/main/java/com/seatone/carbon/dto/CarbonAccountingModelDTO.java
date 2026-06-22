package com.seatone.carbon.dto;

import lombok.Data;

@Data
public class CarbonAccountingModelDTO {

    private Long id;

    private String modelName;

    private Long energyUnitId;

    private String modelConfig;

    private Integer status;

    private String remark;
}