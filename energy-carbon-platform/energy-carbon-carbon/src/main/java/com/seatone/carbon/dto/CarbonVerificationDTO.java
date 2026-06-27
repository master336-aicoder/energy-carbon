package com.seatone.carbon.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
public class CarbonVerificationDTO {

    private Long id;

    @NotBlank(message = "核查名称不能为空")
    private String verificationName;

    @NotNull(message = "关联核算ID不能为空")
    private Long accountingId;

    @NotBlank(message = "核查类型不能为空")
    private String verificationType;

    private String verifier;

    private LocalDate verificationDate;

    private String dataSourceDescription;

    private String attachmentPath;

    private String verificationResult;

    private String status;

    private String remark;
}