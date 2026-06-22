package com.seatone.system.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("ai_report_template")
public class AiReportTemplate {

    private Long id;

    private String templateName;

    private String reportType;

    private String templateContent;

    private String variables;

    private Integer status;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private Integer deleted;
}
