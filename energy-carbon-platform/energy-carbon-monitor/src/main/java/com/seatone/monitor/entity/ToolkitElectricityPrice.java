package com.seatone.monitor.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("toolkit_electricity_price")
public class ToolkitElectricityPrice {

    private Long id;

    private String priceName;

    private LocalDate effectiveDate;

    private BigDecimal peakPrice;

    private BigDecimal flatPrice;

    private BigDecimal valleyPrice;

    private String peakHours;

    private String flatHours;

    private String valleyHours;

    private Integer status;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private Integer deleted;
}
