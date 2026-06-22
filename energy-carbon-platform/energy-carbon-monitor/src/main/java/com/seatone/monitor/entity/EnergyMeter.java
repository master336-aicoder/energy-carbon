package com.seatone.monitor.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.seatone.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("energy_meter")
public class EnergyMeter extends BaseEntity {

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
