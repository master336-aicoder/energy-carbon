package com.seatone.carbon.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class CarbonMarketInfoDTO {

    private Long id;

    private String infoTitle;

    private String infoCategory;

    private String content;

    private String source;

    private LocalDate publishDate;

    private Integer status;

    private String createTime;

    private String updateTime;
}
