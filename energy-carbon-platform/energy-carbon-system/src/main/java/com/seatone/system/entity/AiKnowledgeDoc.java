package com.seatone.system.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("ai_knowledge_doc")
public class AiKnowledgeDoc {

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

    private LocalDateTime updateTime;

    private Integer deleted;
}
