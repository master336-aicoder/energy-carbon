package com.seatone.monitor.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
public class IotDeviceDTO {

    private Long id;

    @NotBlank(message = "设备编码不能为空")
    private String deviceCode;

    @NotBlank(message = "设备名称不能为空")
    private String deviceName;

    @NotNull(message = "网关ID不能为空")
    private Long gatewayId;

    private Long meterId;

    private String topic;

    private String dataFormat;

    private Integer status;

    private LocalDateTime lastOnlineTime;

    private String remark;
}
