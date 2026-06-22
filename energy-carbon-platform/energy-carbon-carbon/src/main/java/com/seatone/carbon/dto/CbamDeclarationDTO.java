package com.seatone.carbon.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class CbamDeclarationDTO {

    private Long id;

    private String declarationName;

    private String declarationPeriod;

    private String productName;

    private String productCode;

    private String cnCode;

    private String originCountry;

    private BigDecimal importQuantity;

    private String unit;

    private BigDecimal directEmission;

    private BigDecimal indirectEmission;

    private BigDecimal totalEmission;

    private String emissionUnit;

    private String certificateNumber;

    private String status;

    private String remark;

    private List<CbamDeclarationDetailDTO> details;

    private String createTime;

    private String updateTime;
}
