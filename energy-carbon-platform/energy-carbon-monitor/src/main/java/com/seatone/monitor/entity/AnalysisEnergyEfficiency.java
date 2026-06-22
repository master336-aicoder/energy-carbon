package com.seatone.monitor.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@TableName("analysis_energy_efficiency")
public class AnalysisEnergyEfficiency {

    private Long id;

    private Long energyUnitId;

    private LocalDate statDate;

    private BigDecimal outputValue;

    private String outputUnit;

    private BigDecimal energyConsumption;

    private String energyUnit;

    private BigDecimal intensity;

    private BigDecimal benchmarkValue;

    private String benchmarkUnit;

    private LocalDate createTime;
}