package com.seatone.monitor.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("toolkit_calculator_record")
public class ToolkitCalculatorRecord {

    private Long id;

    private String calcName;

    private String calcType;

    private String inputParamsJson;

    private String resultJson;

    private LocalDateTime createTime;

    private Long createBy;

    private Integer deleted;
}
