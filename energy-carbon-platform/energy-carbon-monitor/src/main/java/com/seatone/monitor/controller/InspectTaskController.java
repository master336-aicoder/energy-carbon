package com.seatone.monitor.controller;

import com.seatone.common.result.Result;
import com.seatone.common.result.PageResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.*;

@Api(tags = "设备巡检管理")
@RestController
@RequestMapping("/monitor/inspect-task")
public class InspectTaskController {

    private static final List<Map<String, Object>> TASKS = new ArrayList<>();
    private static final List<Map<String, Object>> HISTORY = new ArrayList<>();
    private static long ID_SEQ = 1;

    static {
        addTask("1#空压机", "日常巡检", "pending", LocalDateTime.now().plusHours(2));
        addTask("2#变压器", "安全巡检", "pending", LocalDateTime.now().plusHours(4));
        addTask("3#锅炉", "月度巡检", "in_progress", LocalDateTime.now().minusHours(1));
        addTask("4#冷却塔", "日常巡检", "completed", LocalDateTime.now().minusDays(1));
        addTask("5#配电柜", "季度巡检", "overdue", LocalDateTime.now().minusDays(2));
    }

    private static void addTask(String device, String template, String status, LocalDateTime time) {
        Map<String, Object> m = new HashMap<>();
        m.put("id", ID_SEQ++);
        m.put("deviceName", device);
        m.put("templateName", template);
        m.put("status", status);
        m.put("planTime", time.toString());
        m.put("inspector", "张工");
        m.put("items", Arrays.asList(
            new HashMap<String, Object>() {{
    put("name", "运行状态");
    put("result", "normal");
}},
            new HashMap<String, Object>() {{
    put("name", "温度检测");
    put("result", "normal");
}},
            new HashMap<String, Object>() {{
    put("name", "压力检测");
    put("result", "normal");
}},
            new HashMap<String, Object>() {{
    put("name", "异响检查");
    put("result", "normal");
}}
        ));
        TASKS.add(m);
    }

    @ApiOperation("分页查询巡检任务")
    @GetMapping("/list")
    public Result<PageResult<Map<String, Object>>> list(
            @ApiParam("页码") @RequestParam(defaultValue = "1") Long current,
            @ApiParam("每页条数") @RequestParam(defaultValue = "10") Long size,
            @ApiParam("状态") @RequestParam(required = false) String status) {
        List<Map<String, Object>> filtered = new ArrayList<>();
        for (Map<String, Object> t : TASKS) {
            if (status == null || status.isEmpty() || status.equals(t.get("status"))) {
                filtered.add(t);
            }
        }
        int start = (int)((current - 1) * size);
        int end = Math.min(start + size.intValue(), filtered.size());
        List<Map<String, Object>> list = start < filtered.size() ? filtered.subList(start, end) : new ArrayList<>();
        return Result.ok(PageResult.of(current, size, (long)filtered.size(), list));
    }

    @ApiOperation("获取巡检任务详情")
    @GetMapping("/detail")
    public Result<Map<String, Object>> detail(@ApiParam("任务ID") @RequestParam Long id) {
        for (Map<String, Object> t : TASKS) {
            if (id.equals(t.get("id"))) {
                return Result.ok(t);
            }
        }
        return Result.ok(new HashMap<>());
    }

    @ApiOperation("提交巡检结果")
    @PostMapping("/submit")
    public Result<Void> submit(@RequestBody Map<String, Object> dto) {
        Map<String, Object> record = new HashMap<>(dto);
        record.put("submitTime", LocalDateTime.now().toString());
        HISTORY.add(0, record);
        return Result.ok();
    }

    @ApiOperation("查询巡检历史")
    @GetMapping("/history")
    public Result<PageResult<Map<String, Object>>> history(
            @ApiParam("页码") @RequestParam(defaultValue = "1") Long current,
            @ApiParam("每页条数") @RequestParam(defaultValue = "10") Long size) {
        int start = (int)((current - 1) * size);
        int end = Math.min(start + size.intValue(), HISTORY.size());
        List<Map<String, Object>> list = start < HISTORY.size() ? HISTORY.subList(start, end) : new ArrayList<>();
        return Result.ok(PageResult.of(current, size, (long)HISTORY.size(), list));
    }

    @ApiOperation("巡检统计")
    @GetMapping("/stats")
    public Result<Map<String, Object>> stats() {
        Map<String, Object> data = new HashMap<>();
        data.put("total", TASKS.size());
        data.put("completed", TASKS.stream().filter(t -> "completed".equals(t.get("status"))).count());
        data.put("pending", TASKS.stream().filter(t -> "pending".equals(t.get("status"))).count());
        data.put("overdue", TASKS.stream().filter(t -> "overdue".equals(t.get("status"))).count());
        data.put("inProgress", TASKS.stream().filter(t -> "in_progress".equals(t.get("status"))).count());
        data.put("abnormalCount", 2);
        data.put("avgTime", 35);
        data.put("trendLabels", Arrays.asList("06-20","06-21","06-22","06-23","06-24","06-25","06-26"));
        data.put("trendValues", Arrays.asList(5,7,6,8,5,7,5));
        data.put("deviceRanking", Arrays.asList(
            new HashMap<String, Object>() {{
    put("deviceName", "5#配电柜");
    put("abnormalCount", 3);
}},
            new HashMap<String, Object>() {{
    put("deviceName", "3#锅炉");
    put("abnormalCount", 2);
}},
            new HashMap<String, Object>() {{
    put("deviceName", "1#空压机");
    put("abnormalCount", 1);
}}
        ));
        return Result.ok(data);
    }
}
