package com.seatone.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.seatone.common.base.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * AI 智能服务记录实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("ai_service_record")
@ApiModel("AI 智能服务记录")
public class AiServiceRecord extends BaseEntity {

    @TableId(type = IdType.AUTO)
    @ApiModelProperty("主键ID")
    private Long id;

    @ApiModelProperty("服务名称")
    private String serviceName;

    @ApiModelProperty("服务类型")
    private String serviceType;

    @ApiModelProperty("描述")
    private String description;

    @ApiModelProperty("价格")
    private BigDecimal price;

    @ApiModelProperty("状态：1启用 0停用")
    private Integer status;

    @ApiModelProperty("申请人")
    private String applicant;

    @ApiModelProperty("联系方式")
    private String contact;

    @ApiModelProperty("期望日期")
    private String expectedDate;

    @ApiModelProperty("处理结果")
    private String result;

    @ApiModelProperty("备注")
    private String remark;

    @ApiModelProperty("是否删除：0否 1是")
    private Integer deleted;
}
