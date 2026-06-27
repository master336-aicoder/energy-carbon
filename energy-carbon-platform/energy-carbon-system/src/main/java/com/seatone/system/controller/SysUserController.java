package com.seatone.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.seatone.common.result.PageResult;
import com.seatone.common.result.Result;
import com.seatone.system.dto.AssignRoleDTO;
import com.seatone.system.dto.UserDTO;
import com.seatone.system.entity.SysDept;
import com.seatone.system.entity.SysRole;
import com.seatone.system.entity.SysUser;
import com.seatone.system.mapper.SysDeptMapper;
import com.seatone.system.service.SysUserRoleService;
import com.seatone.system.service.SysUserService;
import com.seatone.system.vo.RoleVO;
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

/**
 * 用户管理控制器
 * 提供用户增删改查、角色分配、状态修改、密码重置等功能
 *
 * @author seatone
 * @since 2026-06-22
 */
@Api(tags = "用户管理")
@RestController
@RequestMapping("/system/user")
public class SysUserController {

    /**
     * 用户服务
     */
    @Autowired
    private SysUserService userService;

    /**
     * 用户角色服务
     */
    @Autowired
    private SysUserRoleService userRoleService;

    /**
     * 部门Mapper
     */
    @Autowired
    private SysDeptMapper sysDeptMapper;

    /**
     * 密码加密器
     */
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    /**
     * 分页查询用户列表
     * 支持按用户名和状态筛选
     *
     * @param current  页码，默认1
     * @param size     每页条数，默认10
     * @param username 用户名（模糊查询）
     * @param status   状态
     * @return 分页用户列表
     */
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

    /**
     * 根据ID查询用户
     *
     * @param id 用户ID
     * @return 用户详情
     */
    @ApiOperation("根据ID查询用户")
    @GetMapping("/{id}")
    public Result<UserVO> getById(@ApiParam("用户ID") @PathVariable Long id) {
        SysUser user = userService.getById(id);
        if (user == null) {
            return Result.error("用户不存在");
        }
        UserVO vo = convertToVO(user);
        // 查询用户关联的角色列表
        List<SysRole> roles = userService.getUserRoles(id);
        vo.setRoles(roles.stream().map(r -> {
            RoleVO roleVO = new RoleVO();
            BeanUtils.copyProperties(r, roleVO);
            return roleVO;
        }).collect(Collectors.toList()));
        return Result.ok(vo);
    }

    /**
     * 新增用户
     * 密码使用 BCrypt 加密存储
     *
     * @param dto 用户数据
     * @return 操作结果
     */
    @ApiOperation("新增用户")
    @PostMapping
    public Result<Void> add(@RequestBody UserDTO dto) {
        SysUser user = new SysUser();
        BeanUtils.copyProperties(dto, user);
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        userService.save(user);
        return Result.ok();
    }

    /**
     * 修改用户
     * 密码为空时不修改密码
     *
     * @param id  用户ID
     * @param dto 用户数据
     * @return 操作结果
     */
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

    /**
     * 删除用户
     *
     * @param id 用户ID
     * @return 操作结果
     */
    @ApiOperation("删除用户")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@ApiParam("用户ID") @PathVariable Long id) {
        userService.removeById(id);
        return Result.ok();
    }

    /**
     * 分配角色
     * 为用户分配一个或多个角色
     *
     * @param dto 角色分配参数
     * @return 操作结果
     */
    @ApiOperation("分配角色")
    @PostMapping("/assignRole")
    public Result<Void> assignRole(@RequestBody AssignRoleDTO dto) {
        userRoleService.assignRoles(dto.getUserId(), dto.getRoleIds());
        return Result.ok();
    }

    /**
     * 修改用户状态
     *
     * @param id     用户ID
     * @param status 状态值
     * @return 操作结果
     */
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

    /**
     * 重置密码
     * 将用户密码重置为默认密码 "123456"
     *
     * @param id 用户ID
     * @return 操作结果
     */
    @ApiOperation("重置密码")
    @PutMapping("/{id}/resetPassword")
    public Result<Void> resetPassword(@ApiParam("用户ID") @PathVariable Long id) {
        SysUser user = new SysUser();
        user.setId(id);
        user.setPassword(passwordEncoder.encode("123456"));
        userService.updateById(user);
        return Result.ok();
    }

    /**
     * 将 SysUser 实体转换为 UserVO
     *
     * @param user 用户实体
     * @return 用户视图对象
     */
    private UserVO convertToVO(SysUser user) {
        UserVO vo = new UserVO();
        BeanUtils.copyProperties(user, vo);
        // 查询部门名称
        if (user.getDeptId() != null) {
            try {
                SysDept dept = sysDeptMapper.selectById(user.getDeptId());
                if (dept != null) {
                    vo.setDeptName(dept.getDeptName());
                }
            } catch (Exception e) {
                vo.setDeptName("");
            }
        }
        return vo;
    }
}
