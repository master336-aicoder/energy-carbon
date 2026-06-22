package com.seatone.alarm.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class AlarmThresholdDTO {

    private Long id;

    private String thresholdName;

    private Long energyUnitId;

    private String energyUnitName;

    private String metricType;

    private String compareType;

    private BigDecimal thresholdValue;

    private String alarmLevel;

    private Integer enabled;

    private String notifyMethod;

    private String remark;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}