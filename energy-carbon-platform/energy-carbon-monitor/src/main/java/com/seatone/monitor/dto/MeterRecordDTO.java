package com.seatone.monitor.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class MeterRecordDTO {

    private Long id;

    private Long meterId;

    private String meterName;

    private String meterCode;

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