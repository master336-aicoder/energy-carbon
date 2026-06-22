package com.seatone.monitor.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("monitor_history_data")
public class MonitorHistoryData {

    private Long id;

    private Long meterId;

    private LocalDateTime dataTime;

    private BigDecimal dataValue;

    private String dataType;

    private String unit;

    private LocalDateTime createTime;
}