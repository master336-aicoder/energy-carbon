package com.seatone.monitor.iot.http;

import com.seatone.common.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Api(tags = "HTTP数据接入")
@RestController
@RequestMapping("/iot/http")
public class HttpDataController {

    @Autowired
    private HttpDataService httpDataService;

    @ApiOperation("接收设备数据（JSON格式）")
    @PostMapping("/data")
    public Result<Void> receiveData(@RequestBody Map<String, Object> data) {
        httpDataService.processData(data);
        return Result.ok();
    }

    @ApiOperation("接收批量数据")
    @PostMapping("/batch")
    public Result<Void> receiveBatchData(@RequestBody java.util.List<Map<String, Object>> dataList) {
        for (Map<String, Object> data : dataList) {
            httpDataService.processData(data);
        }
        return Result.ok();
    }
}
