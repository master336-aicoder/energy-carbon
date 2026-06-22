package com.seatone.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.seatone.common.result.PageResult;
import com.seatone.common.result.Result;
import com.seatone.system.dto.AssignMenuDTO;
import com.seatone.system.dto.RoleDTO;
import com.seatone.system.entity.SysRole;
import com.seatone.system.service.SysRoleService;
import com.seatone.system.vo.RoleVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Api(tags = "角色管理")
@RestController
@RequestMapping("/system/role")
public class SysRoleController {

    @Autowired
    private SysRoleService roleService;

    @ApiOperation("分页查询角色列表")
    @GetMapping("/list")
    public Result<PageResult<RoleVO>> list(
            @ApiParam("页码") @RequestParam(defaultValue = "1") Long current,
            @ApiParam("每页条数") @RequestParam(defaultValue = "10") Long size,
            @ApiParam("角色名称") @RequestParam(required = false) String roleName,
            @ApiParam("状态") @RequestParam(required = false) Integer status) {
        Page<SysRole> page = new Page<>(current, size);
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();
        if (roleName != null && !roleName.isEmpty()) {
            wrapper.like(SysRole::getRoleName, roleName);
        }
        if (status != null) {
            wrapper.eq(SysRole::getStatus, status);
        }
        wrapper.orderByAsc(SysRole::getSort);
        Page<SysRole> resultPage = roleService.page(page, wrapper);
        List<RoleVO> records = resultPage.getRecords().stream().map(this::convertToVO).collect(Collectors.toList());
        return Result.ok(PageResult.of(resultPage.getCurrent(), resultPage.getSize(), resultPage.getTotal(), records));
    }

    @ApiOperation("查询所有角色")
    @GetMapping("/all")
    public Result<List<RoleVO>> all() {
        List<SysRole> list = roleService.list(new LambdaQueryWrapper<SysRole>().eq(SysRole::getStatus, 1));
        return Result.ok(list.stream().map(this::convertToVO).collect(Collectors.toList()));
    }

    @ApiOperation("根据ID查询角色")
    @GetMapping("/{id}")
    public Result<RoleVO> getById(@ApiParam("角色ID") @PathVariable Long id) {
        SysRole role = roleService.getById(id);
        if (role == null) {
            return Result.error("角色不存在");
        }
        return Result.ok(convertToVO(role));
    }

    @ApiOperation("新增角色")
    @PostMapping
    public Result<Void> add(@RequestBody RoleDTO dto) {
        SysRole role = new SysRole();
        BeanUtils.copyProperties(dto, role);
        roleService.save(role);
        return Result.ok();
    }

    @ApiOperation("修改角色")
    @PutMapping("/{id}")
    public Result<Void> update(@ApiParam("角色ID") @PathVariable Long id, @RequestBody RoleDTO dto) {
        SysRole role = new SysRole();
        BeanUtils.copyProperties(dto, role);
        role.setId(id);
        roleService.updateById(role);
        return Result.ok();
    }

    @ApiOperation("删除角色")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@ApiParam("角色ID") @PathVariable Long id) {
        roleService.removeById(id);
        return Result.ok();
    }

    @ApiOperation("分配菜单")
    @PostMapping("/assignMenu")
    public Result<Void> assignMenu(@RequestBody AssignMenuDTO dto) {
        roleService.assignMenus(dto.getRoleId(), dto.getMenuIds());
        return Result.ok();
    }

    @ApiOperation("获取角色菜单ID列表")
    @GetMapping("/{id}/menuIds")
    public Result<List<Long>> getMenuIds(@ApiParam("角色ID") @PathVariable Long id) {
        return Result.ok(roleService.getRoleMenuIds(id));
    }

    @ApiOperation("修改角色状态")
    @PutMapping("/{id}/status")
    public Result<Void> changeStatus(@ApiParam("角色ID") @PathVariable Long id,
                                      @ApiParam("状态") @RequestParam Integer status) {
        SysRole role = new SysRole();
        role.setId(id);
        role.setStatus(status);
        roleService.updateById(role);
        return Result.ok();
    }

    private RoleVO convertToVO(SysRole role) {
        RoleVO vo = new RoleVO();
        BeanUtils.copyProperties(role, vo);
        return vo;
    }
}
