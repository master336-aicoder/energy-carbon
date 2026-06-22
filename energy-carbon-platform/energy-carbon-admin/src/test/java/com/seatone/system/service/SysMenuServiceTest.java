package com.seatone.system.service;

import com.seatone.system.entity.SysMenu;
import com.seatone.system.mapper.SysMenuMapper;
import com.seatone.system.service.impl.SysMenuServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * 菜单服务单元测试
 * 测试菜单树构建、用户菜单获取等功能
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("菜单服务测试")
class SysMenuServiceTest {

    @Mock
    private SysMenuMapper sysMenuMapper;

    @InjectMocks
    private SysMenuServiceImpl sysMenuService;

    private List<SysMenu> flatMenus;

    @BeforeEach
    void setUp() {
        // 构建扁平菜单数据
        SysMenu menu1 = new SysMenu();
        menu1.setId(1L);
        menu1.setMenuName("系统管理");
        menu1.setParentId(0L);
        menu1.setSort(1);
        menu1.setPath("/system");
        menu1.setMenuType(1);
        menu1.setStatus(1);

        SysMenu menu2 = new SysMenu();
        menu2.setId(2L);
        menu2.setMenuName("用户管理");
        menu2.setParentId(1L);
        menu2.setSort(1);
        menu2.setPath("/system/user");
        menu2.setComponent("system/User");
        menu2.setMenuType(2);
        menu2.setStatus(1);

        SysMenu menu3 = new SysMenu();
        menu3.setId(3L);
        menu3.setMenuName("角色管理");
        menu3.setParentId(1L);
        menu3.setSort(2);
        menu3.setPath("/system/role");
        menu3.setComponent("system/Role");
        menu3.setMenuType(2);
        menu3.setStatus(1);

        SysMenu menu4 = new SysMenu();
        menu4.setId(4L);
        menu4.setMenuName("能耗监测");
        menu4.setParentId(0L);
        menu4.setSort(2);
        menu4.setPath("/monitor");
        menu4.setMenuType(1);
        menu4.setStatus(1);

        flatMenus = Arrays.asList(menu2, menu4, menu1, menu3);
    }

    @Test
    @DisplayName("构建菜单树 - 正常构建")
    void testBuildMenuTree_Success() {
        List<SysMenu> tree = sysMenuService.buildMenuTree(flatMenus);

        assertNotNull(tree);
        assertEquals(2, tree.size());
        // 验证排序：系统管理(sort=1) 应在 能耗监测(sort=2) 前面
        assertEquals("系统管理", tree.get(0).getMenuName());
        assertEquals("能耗监测", tree.get(1).getMenuName());
    }

    @Test
    @DisplayName("构建菜单树 - 空列表")
    void testBuildMenuTree_Empty() {
        List<SysMenu> tree = sysMenuService.buildMenuTree(Arrays.asList());

        assertNotNull(tree);
        assertEquals(0, tree.size());
    }

    @Test
    @DisplayName("构建菜单树 - 单个菜单")
    void testBuildMenuTree_Single() {
        SysMenu singleMenu = new SysMenu();
        singleMenu.setId(100L);
        singleMenu.setMenuName("独立菜单");
        singleMenu.setParentId(0L);
        singleMenu.setSort(1);

        List<SysMenu> tree = sysMenuService.buildMenuTree(Arrays.asList(singleMenu));

        assertNotNull(tree);
        assertEquals(1, tree.size());
        assertEquals("独立菜单", tree.get(0).getMenuName());
    }

    @Test
    @DisplayName("获取用户路由菜单 - 正常获取")
    void testGetUserRouteMenus_Success() {
        when(sysMenuMapper.selectMenuTreeByUserId(1L)).thenReturn(flatMenus);

        List<SysMenu> result = sysMenuService.getUserRouteMenus(1L);

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(sysMenuMapper, times(1)).selectMenuTreeByUserId(1L);
    }

    @Test
    @DisplayName("获取用户路由菜单 - 无菜单数据")
    void testGetUserRouteMenus_Empty() {
        when(sysMenuMapper.selectMenuTreeByUserId(999L)).thenReturn(Arrays.asList());

        List<SysMenu> result = sysMenuService.getUserRouteMenus(999L);

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @Test
    @DisplayName("获取用户路由菜单 - 返回null")
    void testGetUserRouteMenus_Null() {
        when(sysMenuMapper.selectMenuTreeByUserId(888L)).thenReturn(null);

        List<SysMenu> result = sysMenuService.getUserRouteMenus(888L);

        assertNotNull(result);
        assertEquals(0, result.size());
    }
}
