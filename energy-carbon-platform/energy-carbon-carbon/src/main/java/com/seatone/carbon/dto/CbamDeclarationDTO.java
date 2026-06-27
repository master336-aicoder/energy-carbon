package com.seatone.carbon.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

@Data
public class CbamDeclarationDTO {

    private Long id;

    @NotBlank(message = "申报名称不能为空")
    private String declarationName;

    @NotBlank(message = "申报周期不能为空")
    private String declarationPeriod;

    @NotBlank(message = "产品名称不能为空")
    private String productName;

    private String productCode;

    private String cnCode;

    private String originCountry;

    @NotNull(message = "进口量不能为空")
    private BigDecimal importQuantity;

    @NotBlank(message = "单位不能为空")
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
