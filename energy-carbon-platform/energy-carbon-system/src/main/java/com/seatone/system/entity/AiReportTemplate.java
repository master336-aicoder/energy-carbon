package com.seatone.system.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.seatone.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;


@Data
@EqualsAndHashCode(callSuper = true)
@TableName("ai_report_template")
public class AiReportTemplate extends BaseEntity {

    private String templateName;
    private String templateType;
    private String description;
    private Integer status;
}
