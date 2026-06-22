package com.seatone.carbon.entity;

import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.seatone.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("cbam_declaration")
public class CbamDeclaration extends BaseEntity {

    /** 申报名称 */
    private String declarationName;

    /** 申报周期 */
    private String declarationPeriod;

    /** 产品名称 */
    private String productName;

    /** 产品编码 */
    private String productCode;

    /** CN编码 */
    private String cnCode;

    /** 原产国 */
    private String originCountry;

    /** 进口量 */
    private BigDecimal importQuantity;

    /** 单位 */
    private String unit;

    /** 直接排放(tCO2) */
    private BigDecimal directEmission;

    /** 间接排放(tCO2) */
    private BigDecimal indirectEmission;

    /** 总排放(tCO2) */
    private BigDecimal totalEmission;

    /** 排放单位 */
    private String emissionUnit;

    /** 证书编号 */
    private String certificateNumber;

    /** 状态(DRAFT/SUBMITTED/APPROVED/REJECTED) */
    private String status;

    /** 备注 */
    private String remark;

    @TableLogic
    private Integer deleted;
}
