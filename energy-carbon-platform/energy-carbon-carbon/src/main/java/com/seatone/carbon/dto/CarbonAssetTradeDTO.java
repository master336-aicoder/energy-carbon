package com.seatone.carbon.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class CarbonAssetTradeDTO {

    private Long id;

    private Long assetId;

    private String assetName;

    private String tradeType;

    private BigDecimal tradeQuantity;

    private BigDecimal tradePrice;

    private BigDecimal tradeAmount;

    private LocalDate tradeDate;

    private String counterparty;

    private String status;

    private String remark;

    private String createTime;

    private String updateTime;
}
