package com.seatone.carbon.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("carbon_asset_trade")
public class CarbonAssetTrade {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 碳资产ID */
    private Long assetId;

    /** 交易类型(BUY/SELL/TRANSFER) */
    private String tradeType;

    /** 交易数量 */
    private BigDecimal tradeQuantity;

    /** 交易单价(元) */
    private BigDecimal tradePrice;

    /** 交易金额(元) */
    private BigDecimal tradeAmount;

    /** 交易日期 */
    private LocalDate tradeDate;

    /** 交易对手 */
    private String counterparty;

    /** 状态 */
    private String status;

    /** 备注 */
    private String remark;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}
