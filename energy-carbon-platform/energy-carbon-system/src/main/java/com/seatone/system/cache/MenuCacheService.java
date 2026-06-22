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
 * 按用户ID缓存菜单树，减少重复查询数据库
 * 缓存过期时间：30分钟
 *
 * @author seatone
 * @since 2026-06-22
 */
@Slf4j
@Service
public class MenuCacheService {

    /**
     * 菜单缓存Key前缀
     */
    private static final String MENU_CACHE_PREFIX = "menu:user:";

    /**
     * 菜单缓存过期时间（分钟）
     */
    private static final int MENU_CACHE_TTL = 30;

    /**
     * Redis缓存工具
     */
    @Autowired
    private RedisCache redisCache;

    /**
     * 菜单服务
     */
    @Autowired
    private SysMenuService sysMenuService;

    /**
     * 获取用户菜单列表
     * 优先从缓存获取，缓存不存在则查询数据库并写入缓存
     *
     * @param userId 用户ID
     * @return 菜单列表
     */
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

    /**
     * 刷新指定用户的菜单缓存
     * 清除缓存，下次访问时重新加载
     *
     * @param userId 用户ID
     */
    public void refreshUserMenus(Long userId) {
        String cacheKey = MENU_CACHE_PREFIX + userId;
        redisCache.deleteObject(cacheKey);
        log.debug("已清除用户菜单缓存: userId={}", userId);
    }

    /**
     * 刷新所有用户的菜单缓存
     * 清除所有菜单缓存数据
     */
    public void refreshAllUserMenus() {
        Collection<String> keys = redisCache.keys(MENU_CACHE_PREFIX + "*");
        if (keys != null && !keys.isEmpty()) {
            redisCache.deleteObject(keys);
            log.info("已清除所有用户菜单缓存，共 {} 个", keys.size());
        }
    }
}
