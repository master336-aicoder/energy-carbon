package com.seatone.monitor.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("toolkit_green_factory")
public class ToolkitGreenFactory {

    private Long id;

    private String declarationName;

    private String enterpriseName;

    private Integer declarationYear;

    private String status;

    private Integer progress;

    private LocalDateTime submitTime;

    private String remark;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private Long createBy;

    private Integer deleted;
}
