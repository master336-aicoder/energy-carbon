package com.seatone.monitor.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class MonitorHistoryDataDTO {

    private Long id;

    private Long meterId;

    private String meterName;

    private LocalDateTime dataTime;

    private BigDecimal dataValue;

    private String dataType;

    private String unit;

    private LocalDateTime createTime;
}