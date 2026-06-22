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
 * 启动时加载所有字典数据到Redis，变更时刷新缓存
 */
@Slf4j
@Service
public class DictCacheService implements CommandLineRunner {

    private static final String DICT_CACHE_PREFIX = "dict:data:";
    private static final String DICT_ALL_KEY = "dict:all";

    @Autowired
    private RedisCache redisCache;

    @Autowired
    private SysDictDataService sysDictDataService;

    @Override
    public void run(String... args) {
        log.info("开始加载字典数据缓存...");
        refreshAll();
        log.info("字典数据缓存加载完成");
    }

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

    @SuppressWarnings("unchecked")
    public List<SysDictData> getDictDataByType(String dictType) {
        return redisCache.getCacheObject(DICT_CACHE_PREFIX + dictType);
    }

    public void refreshByType(String dictType) {
        List<SysDictData> dictDataList = sysDictDataService.listByDictType(dictType);
        redisCache.setCacheObject(DICT_CACHE_PREFIX + dictType, dictDataList);
        log.debug("已刷新字典缓存: type={}", dictType);
    }

    public void clearAll() {
        redisCache.deleteObject(DICT_ALL_KEY);
        java.util.Collection<String> keys = redisCache.keys(DICT_CACHE_PREFIX + "*");
        if (keys != null && !keys.isEmpty()) {
            redisCache.deleteObject(keys);
        }
        log.info("已清除所有字典缓存");
    }
}
