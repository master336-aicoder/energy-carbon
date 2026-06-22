package com.seatone.carbon.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("carbon_footprint_product")
public class CarbonFootprintProduct {

    private Long id;

    private Long productId;

    private String lifecycleStage;

    private BigDecimal emission;

    private String emissionUnit;

    private String calcMethod;

    private String dataSource;

    private LocalDate calcDate;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}