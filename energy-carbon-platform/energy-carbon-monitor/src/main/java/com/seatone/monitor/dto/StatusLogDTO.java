package com.seatone.monitor.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class StatusLogDTO {

    private Long id;

    private Long deviceId;

    private String deviceName;

    private String deviceType;

    private Integer onlineStatus;

    private LocalDateTime offlineTime;

    private LocalDateTime onlineTime;

    private Integer duration;

    private LocalDateTime createTime;
}