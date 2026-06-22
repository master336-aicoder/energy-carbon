package com.seatone.system.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel("部门VO")
public class DeptVO {

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

    @ApiModelProperty("子部门")
    private List<DeptVO> children;
}
