package com.seatone.monitor.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.seatone.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("work_shift")
public class WorkShift extends BaseEntity {

    private String shiftName;

    private String shiftCode;

    private LocalTime startTime;

    private LocalTime endTime;

    private LocalTime restStart;

    private LocalTime restEnd;

    private String workDays;

    private Integer status;

    private String remark;
}
