package com.seatone.monitor.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class GreenFactoryItemDTO {

    private Long id;

    private Long declarationId;

    private String itemName;

    private String itemCategory;

    private Integer required;

    private Integer uploaded;

    private String filePath;

    private LocalDateTime uploadTime;

    private String remark;
}
