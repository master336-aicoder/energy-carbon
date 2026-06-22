package com.seatone.carbon.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class CarbonAssetDTO {

    private Long id;

    private String assetType;

    private String assetName;

    private String assetCode;

    private BigDecimal quantity;

    private String unit;

    private LocalDate acquireDate;

    private LocalDate expireDate;

    private String source;

    private BigDecimal cost;

    private String status;

    private String remark;

    private String createTime;

    private String updateTime;
}
