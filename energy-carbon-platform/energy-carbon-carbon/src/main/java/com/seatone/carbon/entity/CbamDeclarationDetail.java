package com.seatone.carbon.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;

@Data
@TableName("cbam_declaration_detail")
public class CbamDeclarationDetail {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 申报ID */
    private Long declarationId;

    /** 材料名称 */
    private String materialName;

    /** 材料类别 */
    private String materialCategory;

    /** 消耗量 */
    private BigDecimal consumption;

    /** 单位 */
    private String unit;

    /** 排放因子 */
    private BigDecimal emissionFactor;

    /** 排放量 */
    private BigDecimal emission;

    /** 数据来源 */
    private String dataSource;
}
