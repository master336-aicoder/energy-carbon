package com.seatone.monitor.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@TableName("analysis_energy_consumption")
public class AnalysisConsumption {

    private Long id;

    private Long energyUnitId;

    private Long meterId;

    private Long energyTypeId;

    private LocalDate statDate;

    private String statPeriod;

    private BigDecimal consumption;

    private String unit;

    private LocalDate createTime;
}