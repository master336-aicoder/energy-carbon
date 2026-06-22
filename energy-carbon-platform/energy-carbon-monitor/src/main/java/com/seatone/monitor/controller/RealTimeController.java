package com.seatone.monitor.controller;

import com.seatone.common.result.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 实时监测控制器
 * 提供实时能耗数据的查询接口，支持按单元和能源类型筛选
 * 当前为模拟数据，实际项目中应从时序数据库（如 InfluxDB）读取
 *
 * @author seatone
 * @since 2026-06-22
 */
@RestController
@RequestMapping("/monitor/realtime")
public class RealTimeController {

    /**
     * 查询实时监测数据列表
     * 支持分页查询和条件筛选
     *
     * @param unitId     单元ID（可选）
     * @param energyType 能源类型（可选）
     * @param page       页码，默认1
     * @param size       每页条数，默认20
     * @return 分页实时数据列表
     */
    @GetMapping("/list")
    public Result<Map<String, Object>> list(
            @RequestParam(required = false) Long unitId,
            @RequestParam(required = false) String energyType,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer size) {

        List<Map<String, Object>> list = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            Map<String, Object> item = new HashMap<>();
            item.put("id", i);
            item.put("dataItemName", "A相电流-" + i);
            item.put("collectValue", 220.5 + i);
            item.put("ratio", 1);
            item.put("realValue", 220.5 + i);
            item.put("unit", "A");
            list.add(item);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("current", page);
        result.put("size", size);
        result.put("total", 100);
        result.put("pages", 5);
        result.put("records", list);

        return Result.ok(result);
    }
}
