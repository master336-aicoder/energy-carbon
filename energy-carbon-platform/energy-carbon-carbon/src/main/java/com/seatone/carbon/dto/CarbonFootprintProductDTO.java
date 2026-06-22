package com.seatone.carbon.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class CarbonFootprintProductDTO {

    private Long id;

    private Long productId;

    private String lifecycleStage;

    private BigDecimal emission;

    private String emissionUnit;

    private String calcMethod;

    private String dataSource;

    private LocalDate calcDate;
}