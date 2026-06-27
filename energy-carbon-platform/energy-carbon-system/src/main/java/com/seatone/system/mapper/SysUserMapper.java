package com.seatone.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.seatone.system.entity.SysRole;
import com.seatone.system.entity.SysUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SysUserMapper extends BaseMapper<SysUser> {

    @Select("SELECT * FROM sys_user WHERE username = #{username} AND status = 1")
    SysUser selectByUsername(String username);

    @Select("SELECT r.* FROM sys_role r " +
            "INNER JOIN sys_user_role ur ON r.id = ur.role_id " +
            "WHERE ur.user_id = #{userId} AND r.status = 1")
    List<SysRole> selectRolesByUserId(@Param("userId") Long userId);
}
