package com.seatone.monitor.entity;

import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("analysis_energy_budget")
public class AnalysisEnergyBudget {

    private Long id;

    private Long energyUnitId;

    private Integer budgetYear;

    private String budgetType;

    private BigDecimal targetValue;

    private String targetUnit;

    private BigDecimal currentValue;

    private BigDecimal completionRate;

    private Integer status;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}