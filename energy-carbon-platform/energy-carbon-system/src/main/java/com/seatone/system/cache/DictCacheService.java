package com.seatone.system.cache;

import com.seatone.common.utils.RedisCache;
import com.seatone.system.entity.SysDictData;
import com.seatone.system.service.SysDictDataService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 字典数据缓存服务
 * 启动时自动加载所有字典数据到 Redis 缓存
 * 提供按类型查询、刷新、清除等缓存管理功能
 * 实现 CommandLineRunner 接口，应用启动时自动执行缓存加载
 *
 * @author seatone
 * @since 2026-06-22
 */
@Slf4j
@Service
public class DictCacheService implements CommandLineRunner {

    /**
     * 字典缓存Key前缀
     */
    private static final String DICT_CACHE_PREFIX = "dict:data:";

    /**
     * 全部字典缓存Key
     */
    private static final String DICT_ALL_KEY = "dict:all";

    /**
     * Redis缓存工具
     */
    @Autowired
    private RedisCache redisCache;

    /**
     * 字典数据服务
     */
    @Autowired
    private SysDictDataService sysDictDataService;

    /**
     * 应用启动时执行，加载所有字典数据到缓存
     *
     * @param args 启动参数
     */
    @Override
    public void run(String... args) {
        log.info("开始加载字典数据缓存...");
        refreshAll();
        log.info("字典数据缓存加载完成");
    }

    /**
     * 刷新所有字典缓存
     * 从数据库加载全部字典数据，按字典类型分组后写入 Redis
     */
    public void refreshAll() {
        List<SysDictData> allDictData = sysDictDataService.list();
        Map<String, List<SysDictData>> dictMap = allDictData.stream()
                .collect(Collectors.groupingBy(SysDictData::getDictType));
        for (Map.Entry<String, List<SysDictData>> entry : dictMap.entrySet()) {
            redisCache.setCacheObject(DICT_CACHE_PREFIX + entry.getKey(), entry.getValue());
        }
        redisCache.setCacheObject(DICT_ALL_KEY, allDictData);
        log.info("已缓存 {} 种字典类型，共 {} 条数据", dictMap.size(), allDictData.size());
    }

    /**
     * 根据字典类型获取字典数据
     *
     * @param dictType 字典类型
     * @return 字典数据列表
     */
    @SuppressWarnings("unchecked")
    public List<SysDictData> getDictDataByType(String dictType) {
        return redisCache.getCacheObject(DICT_CACHE_PREFIX + dictType);
    }

    /**
     * 按类型刷新字典缓存
     *
     * @param dictType 字典类型
     */
    public void refreshByType(String dictType) {
        List<SysDictData> dictDataList = sysDictDataService.listByDictType(dictType);
        redisCache.setCacheObject(DICT_CACHE_PREFIX + dictType, dictDataList);
        log.debug("已刷新字典缓存: type={}", dictType);
    }

    /**
     * 清除所有字典缓存
     */
    public void clearAll() {
        redisCache.deleteObject(DICT_ALL_KEY);
        java.util.Collection<String> keys = redisCache.keys(DICT_CACHE_PREFIX + "*");
        if (keys != null && !keys.isEmpty()) {
            redisCache.deleteObject(keys);
        }
        log.info("已清除所有字典缓存");
    }
}
