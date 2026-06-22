package com.seatone.system.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel("分配菜单请求")
public class AssignMenuDTO {

    @ApiModelProperty("角色ID")
    private Long roleId;

    @ApiModelProperty("菜单ID列表")
    private List<Long> menuIds;
}
