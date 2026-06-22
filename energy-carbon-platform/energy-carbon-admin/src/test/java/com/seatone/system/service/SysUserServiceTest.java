package com.seatone.system.service;

import com.seatone.system.entity.SysUser;
import com.seatone.system.mapper.SysUserMapper;
import com.seatone.system.service.impl.SysUserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * 用户服务单元测试
 * 测试用户CRUD、登录验证、密码重置等功能
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("用户服务测试")
class SysUserServiceTest {

    @Mock
    private SysUserMapper sysUserMapper;

    @InjectMocks
    private SysUserServiceImpl sysUserService;

    private SysUser testUser;

    @BeforeEach
    void setUp() {
        testUser = new SysUser();
        testUser.setId(1L);
        testUser.setUsername("admin");
        testUser.setPassword("encoded_password");
        testUser.setNickname("管理员");
        testUser.setPhone("13800138000");
        testUser.setEmail("admin@seatone.com");
        testUser.setDeptId(1L);
        testUser.setStatus(1);
        testUser.setCreateTime(LocalDateTime.now());
        testUser.setUpdateTime(LocalDateTime.now());
    }

    @Test
    @DisplayName("根据用户名查询用户 - 存在")
    void testGetByUsername_Found() {
        when(sysUserMapper.selectByUsername("admin")).thenReturn(testUser);

        SysUser result = sysUserService.getByUsername("admin");

        assertNotNull(result);
        assertEquals("admin", result.getUsername());
        assertEquals("管理员", result.getNickname());
        verify(sysUserMapper, times(1)).selectByUsername("admin");
    }

    @Test
    @DisplayName("根据用户名查询用户 - 不存在")
    void testGetByUsername_NotFound() {
        when(sysUserMapper.selectByUsername("nonexistent")).thenReturn(null);

        SysUser result = sysUserService.getByUsername("nonexistent");

        assertNull(result);
        verify(sysUserMapper, times(1)).selectByUsername("nonexistent");
    }

    @Test
    @DisplayName("根据用户名查询用户 - 空用户名")
    void testGetByUsername_Empty() {
        when(sysUserMapper.selectByUsername("")).thenReturn(null);

        SysUser result = sysUserService.getByUsername("");

        assertNull(result);
    }

    @Test
    @DisplayName("新增用户")
    void testAddUser() {
        when(sysUserMapper.insert(any(SysUser.class))).thenReturn(1);

        boolean result = sysUserService.save(testUser);

        assertTrue(result);
        verify(sysUserMapper, times(1)).insert(any(SysUser.class));
    }

    @Test
    @DisplayName("更新用户信息")
    void testUpdateUser() {
        when(sysUserMapper.updateById(any(SysUser.class))).thenReturn(1);

        testUser.setNickname("超级管理员");
        boolean result = sysUserService.updateById(testUser);

        assertTrue(result);
        verify(sysUserMapper, times(1)).updateById(any(SysUser.class));
    }

    @Test
    @DisplayName("删除用户")
    void testDeleteUser() {
        when(sysUserMapper.deleteById(1L)).thenReturn(1);

        boolean result = sysUserService.removeById(1L);

        assertTrue(result);
        verify(sysUserMapper, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("根据ID查询用户")
    void testGetUserById() {
        when(sysUserMapper.selectById(1L)).thenReturn(testUser);

        SysUser result = sysUserService.getById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("admin", result.getUsername());
    }
}
