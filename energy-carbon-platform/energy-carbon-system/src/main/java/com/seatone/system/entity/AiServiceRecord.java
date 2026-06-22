package com.seatone.system.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("ai_service_record")
public class AiServiceRecord {

    private Long id;

    private String serviceName;

    private String serviceType;

    private String applicant;

    private String contact;

    private String description;

    private LocalDate expectedDate;

    private String status;

    private String result;

    private String remark;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private Long createBy;

    private Long updateBy;

    private Integer deleted;
}
