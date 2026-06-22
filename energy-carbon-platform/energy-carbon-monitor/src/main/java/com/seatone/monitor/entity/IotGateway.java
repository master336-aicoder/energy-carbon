package com.seatone.monitor.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.seatone.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("iot_gateway")
public class IotGateway extends BaseEntity {

    private String gatewayCode;

    private String gatewayName;

    private String protocolType;

    private String host;

    private Integer port;

    private String username;

    private String password;

    private Integer status;

    private LocalDateTime lastOnlineTime;

    private String remark;
}
