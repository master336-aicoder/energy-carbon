package com.seatone.system.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.seatone.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("report_schedule")
public class ReportSchedule extends BaseEntity {

    private String scheduleName;
    private String shiftType;
    private LocalDate startDate;
    private LocalDate endDate;
    private String teamName;
    private Integer status;
    private String remark;
}
