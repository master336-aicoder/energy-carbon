package com.seatone.carbon.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
public class CarbonMarketInfoDTO {

    private Long id;

    @NotBlank(message = "信息标题不能为空")
    private String infoTitle;

    @NotBlank(message = "信息分类不能为空")
    private String infoCategory;

    @NotBlank(message = "内容不能为空")
    private String content;

    private String source;

    @NotNull(message = "发布日期不能为空")
    private LocalDate publishDate;

    private Integer status;

    private String createTime;

    private String updateTime;
}
