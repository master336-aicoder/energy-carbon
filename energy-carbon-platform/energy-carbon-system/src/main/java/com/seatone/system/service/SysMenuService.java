package com.seatone.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.seatone.system.entity.SysMenu;

import java.util.List;

public interface SysMenuService extends IService<SysMenu> {

    List<SysMenu> buildMenuTree(List<SysMenu> menus);

    List<SysMenu> getUserRouteMenus(Long userId);
}
