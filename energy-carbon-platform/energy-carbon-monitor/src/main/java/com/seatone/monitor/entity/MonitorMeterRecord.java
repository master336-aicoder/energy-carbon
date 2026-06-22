package com.seatone.monitor.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("monitor_meter_record")
public class MonitorMeterRecord {

    private Long id;

    private Long meterId;

    private LocalDateTime recordTime;

    private BigDecimal startCode;

    private BigDecimal endCode;

    private BigDecimal consumption;

    private BigDecimal cost;

    private BigDecimal unitPrice;

    private String operator;

    private String remark;

    private LocalDateTime createTime;
}