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
@TableName("supply_chain_carbon")
public class SupplyChainCarbon extends BaseEntity {

    /** 供应商名称 */
    private String supplierName;

    /** 供应商编码 */
    private String supplierCode;

    /** 产品名称 */
    private String productName;

    /** 产品类别 */
    private String productCategory;

    /** 碳足迹值 */
    private BigDecimal carbonFootprintValue;

    /** 单位 */
    private String unit;

    /** 认证类型 */
    private String certType;

    /** 认证编号 */
    private String certNumber;

    /** 有效期 */
    private LocalDate validDate;

    /** 数据来源 */
    private String dataSource;

    /** 状态（1正常 0停用） */
    private Integer status;

    /** 备注 */
    private String remark;

    @TableLogic
    private Integer deleted;
}
