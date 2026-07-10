package com.seatone.carbon.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 排放计算请求参数
 */
@Data
public class EmissionCalcDTO {

    private Long accountingId;

    /** 活动数据列表 */
    private List<ActivityDataItem> activities;

    @Data
    public static class ActivityDataItem {
        private String activityName;
        private Long energyTypeId;
        private BigDecimal activityData;
        private BigDecimal originalValue;
        private String activityUnit;
        private Long factorId;
        private String sourceDescription;
        private String category;
        private String emissionSource;
        private String dataSource;
        private String dataQuality;
        private String verifyStatus;
        private String operator;
        private String relatedIndicator;
        private String attachmentUrl;
        private String calcFormula;
    }
}
