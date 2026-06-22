package com.seatone.system.cache;

import com.seatone.common.utils.RedisCache;
import com.seatone.system.entity.SysMenu;
import com.seatone.system.service.SysMenuService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 菜单数据缓存服务
 * 按用户ID缓存菜单树，减少重复查询
 */
@Slf4j
@Service
public class MenuCacheService {

    private static final String MENU_CACHE_PREFIX = "menu:user:";
    private static final int MENU_CACHE_TTL = 30;

    @Autowired
    private RedisCache redisCache;

    @Autowired
    private SysMenuService sysMenuService;

    @SuppressWarnings("unchecked")
    public List<SysMenu> getUserMenus(Long userId) {
        String cacheKey = MENU_CACHE_PREFIX + userId;
        List<SysMenu> menus = redisCache.getCacheObject(cacheKey);
        if (menus != null) {
            log.debug("菜单缓存命中: userId={}", userId);
            return menus;
        }
        menus = sysMenuService.getUserRouteMenus(userId);
        redisCache.setCacheObject(cacheKey, menus, MENU_CACHE_TTL, TimeUnit.MINUTES);
        return menus;
    }

    public void refreshUserMenus(Long userId) {
        String cacheKey = MENU_CACHE_PREFIX + userId;
        redisCache.deleteObject(cacheKey);
        log.debug("已清除用户菜单缓存: userId={}", userId);
    }

    public void refreshAllUserMenus() {
        Collection<String> keys = redisCache.keys(MENU_CACHE_PREFIX + "*");
        if (keys != null && !keys.isEmpty()) {
            redisCache.deleteObject(keys);
            log.info("已清除所有用户菜单缓存，共 {} 个", keys.size());
        }
    }
}
