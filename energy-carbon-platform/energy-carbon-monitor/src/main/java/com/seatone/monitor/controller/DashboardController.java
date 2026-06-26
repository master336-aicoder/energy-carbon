package com.seatone.monitor.controller;

import com.seatone.common.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@Api(tags = "仪表盘")
@RestController
@RequestMapping("/dashboard")
public class DashboardController {

    @ApiOperation("获取首页KPI数据")
    @GetMapping("/kpi")
    public Result<Map<String, Object>> kpi() {
        Map<String, Object> data = new HashMap<>();
        data.put("todayEnergy", 12580.5);
        data.put("todayCarbon", 8.32);
        data.put("deviceOnline", 156);
        data.put("deviceTotal", 168);
        data.put("alarmUnhandled", 3);
        data.put("energyTrend", Arrays.asList(
            new HashMap<String, Object>() {{
    put("date", "06-20");
    put("value", 11200.0);
}},
            new HashMap<String, Object>() {{
    put("date", "06-21");
    put("value", 11850.0);
}},
            new HashMap<String, Object>() {{
    put("date", "06-22");
    put("value", 12100.0);
}},
            new HashMap<String, Object>() {{
    put("date", "06-23");
    put("value", 11980.0);
}},
            new HashMap<String, Object>() {{
    put("date", "06-24");
    put("value", 12400.0);
}},
            new HashMap<String, Object>() {{
    put("date", "06-25");
    put("value", 12250.0);
}},
            new HashMap<String, Object>() {{
    put("date", "06-26");
    put("value", 12580.5);
}}
        ));
        return Result.ok(data);
    }
}
