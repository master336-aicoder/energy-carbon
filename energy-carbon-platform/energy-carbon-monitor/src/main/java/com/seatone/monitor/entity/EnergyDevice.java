package com.seatone.monitor.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.seatone.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("energy_device")
public class EnergyDevice extends BaseEntity {

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
