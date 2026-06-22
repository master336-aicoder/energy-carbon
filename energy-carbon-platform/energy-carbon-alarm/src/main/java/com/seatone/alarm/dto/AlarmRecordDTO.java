package com.seatone.alarm.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class AlarmRecordDTO {

    private Long id;

    private Long thresholdId;

    private String thresholdName;

    private Long meterId;

    private String meterName;

    private String alarmType;

    private String alarmLevel;

    private BigDecimal alarmValue;

    private BigDecimal thresholdValue;

    private LocalDateTime alarmTime;

    private String handleStatus;

    private String handler;

    private LocalDateTime handleTime;

    private String handleRemark;

    private LocalDateTime createTime;
}