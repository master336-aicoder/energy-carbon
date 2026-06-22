package com.seatone.report.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("report_energy_saving")
public class ReportEnergySaving {

    private Long id;

    private Long energyUnitId;

    private String reportType;

    private LocalDate statDate;

    private BigDecimal baselineConsumption;

    private BigDecimal actualConsumption;

    private BigDecimal savingAmount;

    private BigDecimal savingRate;

    private String savingAmountUnit;

    private LocalDateTime createTime;
}