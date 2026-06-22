package com.seatone.monitor.dto;

import lombok.Data;

import java.time.LocalTime;

@Data
public class WorkShiftDTO {

    private Long id;

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
