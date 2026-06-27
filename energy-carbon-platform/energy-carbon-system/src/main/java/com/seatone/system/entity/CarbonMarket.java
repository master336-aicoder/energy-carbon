package com.seatone.system.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.seatone.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("carbon_market")
public class CarbonMarket extends BaseEntity {

    private Integer tradeType;
    private LocalDate tradeDate;
    private BigDecimal tradeVolume;
    private BigDecimal tradePrice;
    private BigDecimal totalAmount;
    private String buyer;
    private String seller;
    private Integer status;
    private String remark;
}
