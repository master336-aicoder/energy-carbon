package com.seatone.monitor.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class EnergyMeterDTO {

    private Long id;

    private String meterCode;

    private String meterName;

    private String meterType;

    private Long deviceId;

    private Long deptId;

    private Integer status;

    private String accuracyClass;

    private BigDecimal rangeMin;

    private BigDecimal rangeMax;

    private LocalDate installDate;

    private LocalDate calibrationDate;

    private LocalDate nextCalibrationDate;

    private String remark;
}
