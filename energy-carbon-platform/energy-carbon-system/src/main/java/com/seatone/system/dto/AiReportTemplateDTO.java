package com.seatone.system.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AiReportTemplateDTO {

    private Long id;

    private String templateName;

    private String reportType;

    private String templateContent;

    private String variables;

    private Integer status;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
