package com.seatone.system.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel("菜单VO")
public class MenuVO {

    @ApiModelProperty("菜单ID")
    private Long id;

    @ApiModelProperty("菜单名称")
    private String menuName;

    @ApiModelProperty("父菜单ID")
    private Long parentId;

    @ApiModelProperty("排序")
    private Integer sort;

    @ApiModelProperty("路由路径")
    private String path;

    @ApiModelProperty("组件路径")
    private String component;

    @ApiModelProperty("图标")
    private String icon;

    @ApiModelProperty("菜单类型：1-目录，2-菜单，3-按钮")
    private Integer menuType;

    @ApiModelProperty("状态：0-禁用，1-启用")
    private Integer status;

    @ApiModelProperty("权限标识")
    private String permission;

    @ApiModelProperty("是否隐藏：0-否，1-是")
    private Integer hidden;

    @ApiModelProperty("是否缓存：0-否，1-是")
    private Integer keepAlive;

    @ApiModelProperty("子菜单")
    private List<MenuVO> children;
}
