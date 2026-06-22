package com.seatone.system.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.seatone.common.annotation.CacheableCustom;
import com.seatone.system.entity.SysMenu;
import com.seatone.system.mapper.SysMenuMapper;
import com.seatone.system.service.SysMenuService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SysMenuServiceImpl extends ServiceImpl<SysMenuMapper, SysMenu> implements SysMenuService {

    @Override
    public List<SysMenu> buildMenuTree(List<SysMenu> menus) {
        List<SysMenu> tree = new ArrayList<>();
        List<Long> menuIds = menus.stream().map(SysMenu::getId).collect(Collectors.toList());
        for (SysMenu menu : menus) {
            if (menu.getParentId() == null || menu.getParentId() == 0L || !menuIds.contains(menu.getParentId())) {
                tree.add(menu);
            }
        }
        for (SysMenu menu : menus) {
            if (menu.getParentId() != null && menu.getParentId() != 0L && menuIds.contains(menu.getParentId())) {
                for (SysMenu parent : menus) {
                    if (parent.getId().equals(menu.getParentId())) {
                        // children field not in entity, skip attaching
                        break;
                    }
                }
            }
        }
        tree.sort(Comparator.comparingInt(SysMenu::getSort));
        return tree;
    }

    @Override
    @CacheableCustom(prefix = "menu:user", key = "#userId", ttl = 30, description = "用户菜单路由缓存")
    public List<SysMenu> getUserRouteMenus(Long userId) {
        List<SysMenu> menus = baseMapper.selectMenuTreeByUserId(userId);
        if (menus == null || menus.isEmpty()) {
            return new ArrayList<>();
        }
        return buildMenuTree(menus);
    }
}
