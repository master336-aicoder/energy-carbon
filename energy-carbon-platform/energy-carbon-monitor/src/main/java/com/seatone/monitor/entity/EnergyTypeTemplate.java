package com.seatone.monitor.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.seatone.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("energy_type_template")
public class EnergyTypeTemplate extends BaseEntity {

    private String templateName;

    private String templateCode;

    private String typeIds;

    private Integer status;
}
