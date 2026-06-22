package com.seatone.carbon.entity;

import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.seatone.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("carbon_verification")
public class CarbonVerification extends BaseEntity {

    private String verificationName;

    private Long accountingId;

    private String verificationType;

    private String verifier;

    private LocalDate verificationDate;

    private String dataSourceDescription;

    private String attachmentPath;

    private String verificationResult;

    private String status;

    private String remark;

    @TableLogic
    private Integer deleted;
}