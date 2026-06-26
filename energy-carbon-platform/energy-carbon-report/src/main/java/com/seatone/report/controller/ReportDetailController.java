package com.seatone.report.controller;

import com.seatone.common.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@Api(tags = "报表详情")
@RestController
@RequestMapping("/report/detail")
public class ReportDetailController {

    @ApiOperation("获取报表详情数据")
    @GetMapping
    public Result<Map<String, Object>> detail(
            @ApiParam("报表类型") @RequestParam String type,
            @ApiParam("时间维度") @RequestParam String dimension,
            @ApiParam("日期") @RequestParam String date) {
        Map<String, Object> data = new HashMap<>();
        data.put("type", type);
        data.put("dimension", dimension);
        data.put("date", date);
        data.put("total", 12580.5);
        data.put("yoy", 8.5);
        data.put("mom", -2.3);
        data.put("chartLabels", Arrays.asList("1日","2日","3日","4日","5日","6日","7日"));
        data.put("chartValues", Arrays.asList(1800,1950,2100,1850,2200,2050,1830.5));
        data.put("chartValuesLast", Arrays.asList(1600,1750,1900,1700,2000,1950,1680));
        return Result.ok(data);
    }
}
