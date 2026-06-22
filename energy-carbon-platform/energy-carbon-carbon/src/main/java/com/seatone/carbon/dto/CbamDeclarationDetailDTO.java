package com.seatone.carbon.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CbamDeclarationDetailDTO {

    private Long id;

    private Long declarationId;

    private String materialName;

    private String materialCategory;

    private BigDecimal consumption;

    private String unit;

    private BigDecimal emissionFactor;

    private BigDecimal emission;

    private String dataSource;
}
