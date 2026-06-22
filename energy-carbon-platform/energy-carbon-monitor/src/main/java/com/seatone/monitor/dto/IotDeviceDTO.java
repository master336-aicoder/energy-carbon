package com.seatone.monitor.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class IotDeviceDTO {

    private Long id;

    private String deviceCode;

    private String deviceName;

    private Long gatewayId;

    private Long meterId;

    private String topic;

    private String dataFormat;

    private Integer status;

    private LocalDateTime lastOnlineTime;

    private String remark;
}
