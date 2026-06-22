package com.seatone.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.seatone.system.entity.SysUserRole;

import java.util.List;

public interface SysUserRoleService extends IService<SysUserRole> {

    void assignRoles(Long userId, List<Long> roleIds);

    List<Long> getUserRoleIds(Long userId);
}
