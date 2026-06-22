package com.seatone.monitor.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CalculatorDTO {

    private Long id;

    private String calcName;

    private String calcType;

    private String inputParamsJson;

    private String resultJson;

    private LocalDateTime createTime;
}
