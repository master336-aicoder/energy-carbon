package com.seatone.system.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.seatone.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_menu")
public class SysMenu extends BaseEntity {

    private String menuName;

    private Long parentId;

    private Integer sort;

    private String path;

    private String component;

    private String icon;

    private Integer menuType;

    private Integer status;

    private String permission;

    private Integer hidden;

    private Integer keepAlive;
}
