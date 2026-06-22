package com.seatone.alarm.entity;

import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("alarm_threshold")
public class AlarmThreshold {

    private Long id;

    private String thresholdName;

    private Long energyUnitId;

    private String metricType;

    private String compareType;

    private BigDecimal thresholdValue;

    private String alarmLevel;

    private Integer enabled;

    private String notifyMethod;

    private String remark;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}