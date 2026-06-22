package com.seatone.system.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("部门DTO")
public class DeptDTO {

    @ApiModelProperty("部门ID")
    private Long id;

    @ApiModelProperty("部门名称")
    private String deptName;

    @ApiModelProperty("父部门ID")
    private Long parentId;

    @ApiModelProperty("排序")
    private Integer sort;

    @ApiModelProperty("负责人")
    private String leader;

    @ApiModelProperty("联系电话")
    private String phone;

    @ApiModelProperty("邮箱")
    private String email;

    @ApiModelProperty("状态：0-禁用，1-启用")
    private Integer status;
}
