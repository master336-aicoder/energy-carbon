package com.seatone.system.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.seatone.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("monitor_device")
public class MonitorDevice extends BaseEntity {

    private String deviceName;
    private String deviceType;
    private String deviceCode;
    private Integer status;
    private String location;
    private LocalDate installTime;
    private String remark;
}
