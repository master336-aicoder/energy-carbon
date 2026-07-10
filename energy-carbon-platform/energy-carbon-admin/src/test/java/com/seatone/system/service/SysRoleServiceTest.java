package com.seatone.system.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.seatone.system.entity.SysRole;
import com.seatone.system.entity.SysRoleMenu;
import com.seatone.system.mapper.SysRoleMapper;
import com.seatone.system.mapper.SysRoleMenuMapper;
import com.seatone.system.service.impl.SysRoleServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * 角色服务单元测试
 * 测试角色CRUD、菜单分配等功能
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("角色服务测试")
class SysRoleServiceTest {

    @Mock
    private SysRoleMapper sysRoleMapper;

    @Mock
    private SysRoleMenuMapper sysRoleMenuMapper;

    @InjectMocks
    private SysRoleServiceImpl sysRoleService;

    private SysRole testRole;

    @BeforeEach
    void setUp() throws Exception {
        Field baseMapperField = ServiceImpl.class.getDeclaredField("baseMapper");
        baseMapperField.setAccessible(true);
        baseMapperField.set(sysRoleService, sysRoleMapper);

        Field entityClassField = ServiceImpl.class.getDeclaredField("entityClass");
        entityClassField.setAccessible(true);
        entityClassField.set(sysRoleService, SysRole.class);

        testRole = new SysRole();
        testRole.setId(1L);
        testRole.setRoleName("管理员");
        testRole.setRoleCode("admin");
        testRole.setRemark("系统管理员角色");
        testRole.setSort(1);
        testRole.setStatus(1);
        testRole.setCreateTime(LocalDateTime.now());
        testRole.setUpdateTime(LocalDateTime.now());
    }

    @Test
    @DisplayName("分配菜单 - 正常分配")
    void testAssignMenus_Success() {
        when(sysRoleMenuMapper.delete(any(LambdaQueryWrapper.class))).thenReturn(1);
        when(sysRoleMenuMapper.insert(any(SysRoleMenu.class))).thenReturn(1);

        List<Long> menuIds = Arrays.asList(1L, 2L, 3L);
        sysRoleService.assignMenus(1L, menuIds);

        verify(sysRoleMenuMapper, times(1)).delete(any(LambdaQueryWrapper.class));
        verify(sysRoleMenuMapper, times(3)).insert(any(SysRoleMenu.class));
    }

    @Test
    @DisplayName("分配菜单 - 空菜单列表")
    void testAssignMenus_EmptyList() {
        when(sysRoleMenuMapper.delete(any(LambdaQueryWrapper.class))).thenReturn(1);

        sysRoleService.assignMenus(1L, Arrays.asList());

        verify(sysRoleMenuMapper, times(1)).delete(any(LambdaQueryWrapper.class));
        verify(sysRoleMenuMapper, never()).insert(any(SysRoleMenu.class));
    }

    @Test
    @DisplayName("分配菜单 - null菜单列表")
    void testAssignMenus_NullList() {
        when(sysRoleMenuMapper.delete(any(LambdaQueryWrapper.class))).thenReturn(1);

        sysRoleService.assignMenus(1L, null);

        verify(sysRoleMenuMapper, times(1)).delete(any(LambdaQueryWrapper.class));
        verify(sysRoleMenuMapper, never()).insert(any(SysRoleMenu.class));
    }

    @Test
    @DisplayName("获取角色菜单ID列表")
    void testGetRoleMenuIds() {
        List<Long> expectedIds = Arrays.asList(1L, 2L, 3L, 5L);
        when(sysRoleMenuMapper.selectMenuIdsByRoleId(1L)).thenReturn(expectedIds);

        List<Long> result = sysRoleService.getRoleMenuIds(1L);

        assertNotNull(result);
        assertEquals(4, result.size());
        assertTrue(result.contains(1L));
        assertTrue(result.contains(3L));
        verify(sysRoleMenuMapper, times(1)).selectMenuIdsByRoleId(1L);
    }

    @Test
    @DisplayName("新增角色")
    void testAddRole() {
        when(sysRoleMapper.insert(any(SysRole.class))).thenReturn(1);

        boolean result = sysRoleService.save(testRole);

        assertTrue(result);
        verify(sysRoleMapper, times(1)).insert(any(SysRole.class));
    }

    @Test
    @DisplayName("更新角色")
    void testUpdateRole() {
        when(sysRoleMapper.updateById(any(SysRole.class))).thenReturn(1);

        testRole.setRoleName("超级管理员");
        boolean result = sysRoleService.updateById(testRole);

        assertTrue(result);
        verify(sysRoleMapper, times(1)).updateById(any(SysRole.class));
    }

}
