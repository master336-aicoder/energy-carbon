package com.seatone.monitor.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
public class IotGatewayDTO {

    private Long id;

    @NotBlank(message = "网关编码不能为空")
    private String gatewayCode;

    @NotBlank(message = "网关名称不能为空")
    private String gatewayName;

    @NotBlank(message = "协议类型不能为空")
    private String protocolType;

    @NotBlank(message = "主机地址不能为空")
    private String host;

    @NotNull(message = "端口不能为空")
    private Integer port;

    private String username;

    private String password;

    private Integer status;

    private LocalDateTime lastOnlineTime;

    private String remark;
}
