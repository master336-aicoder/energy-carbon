package com.seatone.system.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.seatone.system.entity.SysUser;
import com.seatone.system.mapper.SysUserMapper;
import com.seatone.system.service.SysUserService;
import org.springframework.stereotype.Service;

/**
 * 用户服务实现类
 * 继承 MyBatis-Plus ServiceImpl 提供基础CRUD能力
 * 提供按用户名查询用户的业务方法
 *
 * @author seatone
 * @since 2026-06-22
 */
@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {

    /**
     * 根据用户名查询用户
     *
     * @param username 用户名
     * @return 用户实体，不存在返回 null
     */
    @Override
    public SysUser getByUsername(String username) {
        return baseMapper.selectByUsername(username);
    }
}
