package com.seatone.carbon.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class CarbonVerificationDTO {

    private Long id;

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
}