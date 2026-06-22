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

@RestController
@RequestMapping("/monitor/realtime")
public class RealTimeController {

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
