package com.seatone.system.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.seatone.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;


@Data
@EqualsAndHashCode(callSuper = true)
@TableName("monitor_work_shift")
public class MonitorWorkShift extends BaseEntity {

    private String shiftName;
    private String shiftCode;
    private String startTime;
    private String endTime;
    private String leader;
    private String members;
    private Integer status;
    private String remark;
}
