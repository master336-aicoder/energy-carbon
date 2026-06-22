package com.seatone.carbon.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class SupplyChainCarbonDTO {

    private Long id;

    private String supplierName;

    private String supplierCode;

    private String productName;

    private String productCategory;

    private BigDecimal carbonFootprintValue;

    private String unit;

    private String certType;

    private String certNumber;

    private LocalDate validDate;

    private String dataSource;

    private Integer status;

    private String remark;

    private String createTime;

    private String updateTime;
}
