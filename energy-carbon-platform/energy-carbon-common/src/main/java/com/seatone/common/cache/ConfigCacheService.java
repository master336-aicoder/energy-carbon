package com.seatone.common.cache;

import com.seatone.common.utils.RedisCache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 系统配置缓存服务
 * 缓存系统配置参数，支持运行时动态更新
 * 采用二级缓存策略：本地缓存（快速读取）+ Redis缓存（分布式共享）
 *
 * @author seatone
 * @since 2026-06-22
 */
@Slf4j
@Service
public class ConfigCacheService {

    /**
     * 配置缓存Key前缀
     */
    private static final String CONFIG_CACHE_PREFIX = "config:";

    /**
     * 全部配置缓存Key
     */
    private static final String CONFIG_ALL_KEY = "config:all";

    /**
     * Redis缓存工具
     */
    @Autowired
    private RedisCache redisCache;

    /**
     * 本地配置缓存（用于快速读取，减少Redis访问）
     */
    private final Map<String, String> localCache = new ConcurrentHashMap<>();

    /**
     * 获取配置值
     * 优先从本地缓存获取，本地缓存不存在则从Redis获取并写入本地缓存
     *
     * @param configKey 配置键
     * @return 配置值，不存在返回 null
     */
    public String getConfig(String configKey) {
        // 先从本地缓存获取
        String localValue = localCache.get(configKey);
        if (localValue != null) {
            return localValue;
        }

        // 从Redis获取
        String redisValue = redisCache.getCacheObject(CONFIG_CACHE_PREFIX + configKey);
        if (redisValue != null) {
            localCache.put(configKey, redisValue);
        }
        return redisValue;
    }

    /**
     * 设置配置值
     * 同时更新Redis缓存和本地缓存
     *
     * @param configKey   配置键
     * @param configValue 配置值
     */
    public void setConfig(String configKey, String configValue) {
        redisCache.setCacheObject(CONFIG_CACHE_PREFIX + configKey, configValue);
        localCache.put(configKey, configValue);
        log.debug("更新配置缓存: key={}", configKey);
    }

    /**
     * 删除配置
     * 同时删除Redis缓存和本地缓存
     *
     * @param configKey 配置键
     */
    public void removeConfig(String configKey) {
        redisCache.deleteObject(CONFIG_CACHE_PREFIX + configKey);
        localCache.remove(configKey);
        log.debug("删除配置缓存: key={}", configKey);
    }

    /**
     * 刷新所有本地缓存（从Redis重新加载）
     * 用于配置变更后同步本地缓存
     */
    @SuppressWarnings("unchecked")
    public void refreshLocalCache() {
        Map<String, String> allConfigs = redisCache.getCacheObject(CONFIG_ALL_KEY);
        if (allConfigs != null) {
            localCache.clear();
            localCache.putAll(allConfigs);
            log.info("已刷新本地配置缓存，共 {} 项", allConfigs.size());
        }
    }

    /**
     * 清除所有配置缓存
     * 同时清除本地缓存和Redis缓存
     */
    public void clearAll() {
        localCache.clear();
        redisCache.deleteObject(CONFIG_ALL_KEY);
        Collection<String> keys = redisCache.keys(CONFIG_CACHE_PREFIX + "*");
        if (keys != null && !keys.isEmpty()) {
            redisCache.deleteObject(keys);
        }
        log.info("已清除所有配置缓存");
    }
}
