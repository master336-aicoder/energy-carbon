package com.seatone.system.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.seatone.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("ai_service")
public class AiService extends BaseEntity {

    private String serviceName;
    private String serviceType;
    private String description;
    private Integer status;
    private BigDecimal price;
}
