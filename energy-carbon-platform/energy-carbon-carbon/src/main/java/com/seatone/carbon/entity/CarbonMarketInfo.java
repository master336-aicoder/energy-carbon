package com.seatone.carbon.entity;

import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.seatone.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("carbon_market_info")
public class CarbonMarketInfo extends BaseEntity {

    /** 信息标题 */
    private String infoTitle;

    /** 信息分类(POLICY/MARKET/PRICE/FORECAST) */
    private String infoCategory;

    /** 内容 */
    private String content;

    /** 来源 */
    private String source;

    /** 发布日期 */
    private LocalDate publishDate;

    /** 状态（1启用 0停用） */
    private Integer status;

    @TableLogic
    private Integer deleted;
}
