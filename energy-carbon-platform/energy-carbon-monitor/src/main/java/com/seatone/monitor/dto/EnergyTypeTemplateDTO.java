package com.seatone.monitor.dto;

import lombok.Data;

@Data
public class EnergyTypeTemplateDTO {

    private Long id;

    private String templateName;

    private String templateCode;

    private String typeIds;

    private Integer status;
}
