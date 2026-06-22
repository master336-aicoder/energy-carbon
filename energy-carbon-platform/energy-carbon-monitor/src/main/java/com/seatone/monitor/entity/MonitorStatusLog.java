package com.seatone.monitor.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("monitor_status_log")
public class MonitorStatusLog {

    private Long id;

    private Long deviceId;

    private String deviceType;

    private Integer onlineStatus;

    private LocalDateTime offlineTime;

    private LocalDateTime onlineTime;

    private Integer duration;

    private LocalDateTime createTime;
}