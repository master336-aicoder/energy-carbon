package com.seatone.monitor.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@TableName("analysis_electricity_cost")
public class AnalysisElectricityCost {

    private Long id;

    private Long meterId;

    private LocalDate statDate;

    private BigDecimal peakPower;

    private BigDecimal valleyPower;

    private BigDecimal flatPower;

    private BigDecimal peakCost;

    private BigDecimal valleyCost;

    private BigDecimal flatCost;

    private BigDecimal totalCost;

    private LocalDate createTime;
}