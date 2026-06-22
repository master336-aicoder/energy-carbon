package com.seatone.monitor.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.seatone.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("iot_device")
public class IotDevice extends BaseEntity {

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
