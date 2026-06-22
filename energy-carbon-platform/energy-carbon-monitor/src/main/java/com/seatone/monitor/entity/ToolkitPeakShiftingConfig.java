package com.seatone.monitor.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@TableName("toolkit_peak_shifting_config")
public class ToolkitPeakShiftingConfig {

    private Long id;

    private String configName;

    private LocalTime peakHoursStart;

    private LocalTime peakHoursEnd;

    private LocalTime valleyHoursStart;

    private LocalTime valleyHoursEnd;

    private LocalTime flatHoursStart;

    private LocalTime flatHoursEnd;

    private BigDecimal maxPower;

    private Integer priority;

    private Integer status;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private Integer deleted;
}
