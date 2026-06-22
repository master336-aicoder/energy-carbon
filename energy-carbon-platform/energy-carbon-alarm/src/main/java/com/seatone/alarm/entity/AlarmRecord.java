package com.seatone.alarm.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("alarm_record")
public class AlarmRecord {

    private Long id;

    private Long thresholdId;

    private Long meterId;

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