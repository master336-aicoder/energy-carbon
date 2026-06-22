package com.seatone.system.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.seatone.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_dept")
public class SysDept extends BaseEntity {

    private String deptName;

    private Long parentId;

    private Integer sort;

    private String leader;

    private String phone;

    private String email;

    private Integer status;

    private String ancestors;
}
