package com.seatone.system.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("ai_report_task")
public class AiReportTask {

    private Long id;

    private String reportName;

    private String reportType;

    private Long templateId;

    private String paramsJson;

    private String status;

    private String resultPath;

    private String errorMsg;

    private Integer progress;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private Long createBy;

    private Integer deleted;
}
