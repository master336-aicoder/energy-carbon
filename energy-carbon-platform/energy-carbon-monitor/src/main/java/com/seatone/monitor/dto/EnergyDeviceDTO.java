package com.seatone.monitor.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class EnergyDeviceDTO {

    private Long id;

    private String deviceCode;

    private String deviceName;

    private String deviceType;

    private Long deptId;

    private Integer status;

    private String manufacturer;

    private String model;

    private LocalDate installDate;

    private BigDecimal powerRating;

    private String voltageLevel;

    private String remark;
}
