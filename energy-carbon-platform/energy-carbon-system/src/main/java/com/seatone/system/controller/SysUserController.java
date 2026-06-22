package com.seatone.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.seatone.common.result.PageResult;
import com.seatone.common.result.Result;
import com.seatone.system.dto.AssignRoleDTO;
import com.seatone.system.dto.UserDTO;
import com.seatone.system.entity.SysUser;
import com.seatone.system.service.SysUserRoleService;
import com.seatone.system.service.SysUserService;
import com.seatone.system.vo.UserVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Api(tags = "用户管理")
@RestController
@RequestMapping("/system/user")
public class SysUserController {

    @Autowired
    private SysUserService userService;

    @Autowired
    private SysUserRoleService userRoleService;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @ApiOperation("分页查询用户列表")
    @GetMapping("/list")
    public Result<PageResult<UserVO>> list(
            @ApiParam("页码") @RequestParam(defaultValue = "1") Long current,
            @ApiParam("每页条数") @RequestParam(defaultValue = "10") Long size,
            @ApiParam("用户名") @RequestParam(required = false) String username,
            @ApiParam("状态") @RequestParam(required = false) Integer status) {
        Page<SysUser> page = new Page<>(current, size);
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        if (username != null && !username.isEmpty()) {
            wrapper.like(SysUser::getUsername, username);
        }
        if (status != null) {
            wrapper.eq(SysUser::getStatus, status);
        }
        wrapper.orderByDesc(SysUser::getCreateTime);
        Page<SysUser> resultPage = userService.page(page, wrapper);
        List<UserVO> records = resultPage.getRecords().stream().map(this::convertToVO).collect(Collectors.toList());
        return Result.ok(PageResult.of(resultPage.getCurrent(), resultPage.getSize(), resultPage.getTotal(), records));
    }

    @ApiOperation("根据ID查询用户")
    @GetMapping("/{id}")
    public Result<UserVO> getById(@ApiParam("用户ID") @PathVariable Long id) {
        SysUser user = userService.getById(id);
        if (user == null) {
            return Result.error("用户不存在");
        }
        return Result.ok(convertToVO(user));
    }

    @ApiOperation("新增用户")
    @PostMapping
    public Result<Void> add(@RequestBody UserDTO dto) {
        SysUser user = new SysUser();
        BeanUtils.copyProperties(dto, user);
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        userService.save(user);
        return Result.ok();
    }

    @ApiOperation("修改用户")
    @PutMapping("/{id}")
    public Result<Void> update(@ApiParam("用户ID") @PathVariable Long id, @RequestBody UserDTO dto) {
        SysUser user = new SysUser();
        BeanUtils.copyProperties(dto, user);
        user.setId(id);
        if (dto.getPassword() != null && !dto.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(dto.getPassword()));
        } else {
            user.setPassword(null);
        }
        userService.updateById(user);
        return Result.ok();
    }

    @ApiOperation("删除用户")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@ApiParam("用户ID") @PathVariable Long id) {
        userService.removeById(id);
        return Result.ok();
    }

    @ApiOperation("分配角色")
    @PostMapping("/assignRole")
    public Result<Void> assignRole(@RequestBody AssignRoleDTO dto) {
        userRoleService.assignRoles(dto.getUserId(), dto.getRoleIds());
        return Result.ok();
    }

    @ApiOperation("修改用户状态")
    @PutMapping("/{id}/status")
    public Result<Void> changeStatus(@ApiParam("用户ID") @PathVariable Long id,
                                      @ApiParam("状态") @RequestParam Integer status) {
        SysUser user = new SysUser();
        user.setId(id);
        user.setStatus(status);
        userService.updateById(user);
        return Result.ok();
    }

    @ApiOperation("重置密码")
    @PutMapping("/{id}/resetPassword")
    public Result<Void> resetPassword(@ApiParam("用户ID") @PathVariable Long id) {
        SysUser user = new SysUser();
        user.setId(id);
        user.setPassword(passwordEncoder.encode("123456"));
        userService.updateById(user);
        return Result.ok();
    }

    private UserVO convertToVO(SysUser user) {
        UserVO vo = new UserVO();
        BeanUtils.copyProperties(user, vo);
        return vo;
    }
}
