package com.seatone.carbon.entity;

import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.seatone.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("carbon_asset")
public class CarbonAsset extends BaseEntity {

    /** 资产类型(QUOTA/CCER/GREEN_CERT) */
    private String assetType;

    /** 资产名称 */
    private String assetName;

    /** 资产编号 */
    private String assetCode;

    /** 数量 */
    private BigDecimal quantity;

    /** 单位 */
    private String unit;

    /** 获取日期 */
    private LocalDate acquireDate;

    /** 到期日期 */
    private LocalDate expireDate;

    /** 来源 */
    private String source;

    /** 成本(元) */
    private BigDecimal cost;

    /** 状态(AVAILABLE/USED/FROZEN/EXPIRED) */
    private String status;

    /** 备注 */
    private String remark;

    @TableLogic
    private Integer deleted;
}
