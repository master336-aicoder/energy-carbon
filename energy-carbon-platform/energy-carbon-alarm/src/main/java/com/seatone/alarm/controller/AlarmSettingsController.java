package com.seatone.alarm.controller;

import com.seatone.common.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@Api(tags = "告警设置")
@RestController
@RequestMapping("/alarm/settings")
public class AlarmSettingsController {

    private static Map<String, Object> SETTINGS = new HashMap<>();

    static {
        SETTINGS.put("types", Arrays.asList(
            new HashMap<String, Object>() {{
    put("type", "electricity");
    put("name", "用电异常");
    put("enabled", true);
}},
            new HashMap<String, Object>() {{
    put("type", "water");
    put("name", "用水异常");
    put("enabled", true);
}},
            new HashMap<String, Object>() {{
    put("type", "carbon");
    put("name", "碳排放超标");
    put("enabled", true);
}},
            new HashMap<String, Object>() {{
    put("type", "device");
    put("name", "设备故障");
    put("enabled", true);
}},
            new HashMap<String, Object>() {{
    put("type", "communication");
    put("name", "通信异常");
    put("enabled", false);
}}
        ));
        SETTINGS.put("doNotDisturbEnabled", false);
        SETTINGS.put("doNotDisturbStart", "22:00");
        SETTINGS.put("doNotDisturbEnd", "07:00");
        SETTINGS.put("pushMethods", Arrays.asList("notification"));
    }

    @ApiOperation("保存告警设置")
    @PostMapping("/save")
    public Result<Void> save(@RequestBody Map<String, Object> dto) {
        SETTINGS = new HashMap<>(dto);
        return Result.ok();
    }

    @ApiOperation("获取告警设置")
    @GetMapping
    public Result<Map<String, Object>> get() {
        return Result.ok(SETTINGS);
    }
}
