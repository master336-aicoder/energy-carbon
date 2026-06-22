package com.seatone.monitor.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class IotGatewayDTO {

    private Long id;

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
