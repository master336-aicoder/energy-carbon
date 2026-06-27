package com.seatone.carbon.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class SupplyChainCarbonDTO {

    private Long id;

    @NotBlank(message = "供应商名称不能为空")
    private String supplierName;

    @NotBlank(message = "供应商编码不能为空")
    private String supplierCode;

    @NotBlank(message = "产品名称不能为空")
    private String productName;

    @NotBlank(message = "产品类别不能为空")
    private String productCategory;

    @NotNull(message = "碳足迹值不能为空")
    private BigDecimal carbonFootprintValue;

    @NotBlank(message = "单位不能为空")
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
