package com.seatone.system.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.seatone.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("carbon_supply_chain")
public class CarbonSupplyChain extends BaseEntity {

    private String supplierName;
    private String materialName;
    private BigDecimal carbonIntensity;
    private String unit;
    private BigDecimal supplyVolume;
    private Integer reportYear;
    private Integer status;
    private String remark;
}
