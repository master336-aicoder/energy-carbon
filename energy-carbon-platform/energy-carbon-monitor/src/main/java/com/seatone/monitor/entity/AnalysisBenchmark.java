package com.seatone.monitor.entity;

import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("analysis_benchmark")
public class AnalysisBenchmark {

    private Long id;

    private String benchmarkName;

    private String industry;

    private Long energyTypeId;

    private String energyCategory;

    private BigDecimal benchmarkValue;

    private String benchmarkUnit;

    private String source;

    private LocalDate effectiveDate;

    private LocalDate expireDate;

    private String remark;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}