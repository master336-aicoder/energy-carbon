package com.seatone.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.seatone.system.entity.SysUser;

public interface SysUserService extends IService<SysUser> {

    SysUser getByUsername(String username);
}
