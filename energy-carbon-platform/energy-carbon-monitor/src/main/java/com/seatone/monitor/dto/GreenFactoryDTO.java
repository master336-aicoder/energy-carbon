package com.seatone.monitor.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class GreenFactoryDTO {

    private Long id;

    private String declarationName;

    private String enterpriseName;

    private Integer declarationYear;

    private String status;

    private Integer progress;

    private LocalDateTime submitTime;

    private String remark;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private List<GreenFactoryItemDTO> items;
}
