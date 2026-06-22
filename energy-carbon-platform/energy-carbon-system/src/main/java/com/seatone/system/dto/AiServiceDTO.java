package com.seatone.system.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class AiServiceDTO {

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
}
