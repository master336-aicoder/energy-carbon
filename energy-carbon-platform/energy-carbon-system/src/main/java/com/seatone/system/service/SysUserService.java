package com.seatone.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.seatone.system.entity.SysRole;
import com.seatone.system.entity.SysUser;

import java.util.List;

public interface SysUserService extends IService<SysUser> {

    SysUser getByUsername(String username);

    List<SysRole> getUserRoles(Long userId);
}
