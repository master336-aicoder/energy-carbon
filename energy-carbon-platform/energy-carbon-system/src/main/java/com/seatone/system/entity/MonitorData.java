package com.seatone.system.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.seatone.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("monitor_data")
public class MonitorData extends BaseEntity {

    private String meterName;
    private String dataType;
    private BigDecimal dataValue;
    private String unit;
    private LocalDateTime collectTime;
    private Integer status;
    private String remark;
}
