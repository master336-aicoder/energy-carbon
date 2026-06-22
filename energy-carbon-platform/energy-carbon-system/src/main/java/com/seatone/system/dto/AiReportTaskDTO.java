package com.seatone.system.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AiReportTaskDTO {

    private Long id;

    private String reportName;

    private String reportType;

    private Long templateId;

    private String templateName;

    private String paramsJson;

    private String status;

    private String resultPath;

    private String errorMsg;

    private Integer progress;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
