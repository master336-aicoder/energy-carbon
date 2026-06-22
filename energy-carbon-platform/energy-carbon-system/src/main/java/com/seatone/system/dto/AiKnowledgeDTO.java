package com.seatone.system.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AiKnowledgeDTO {

    private Long id;

    private String docName;

    private String docType;

    private String filePath;

    private Long fileSize;

    private String summary;

    private String uploadUser;

    private LocalDateTime uploadTime;

    private Integer status;

    private LocalDateTime createTime;
}
