package com.seatone.system.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel("分配角色请求")
public class AssignRoleDTO {

    @ApiModelProperty("用户ID")
    private Long userId;

    @ApiModelProperty("角色ID列表")
    private List<Long> roleIds;
}
