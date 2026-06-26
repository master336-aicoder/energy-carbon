package com.seatone.carbon.controller;

import com.seatone.common.result.Result;
import com.seatone.common.result.PageResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.*;

@Api(tags = "碳排放因子管理")
@RestController
@RequestMapping("/carbon/emission-factor")
public class EmissionFactorController {

    private static final List<Map<String, Object>> DATA = new ArrayList<>();
    private static long ID_SEQ = 1;

    static {
        addDemo("电力", "0.5703", "kgCO2/kWh", "国家标准", LocalDateTime.now().minusDays(30));
        addDemo("天然气", "2.162", "kgCO2/m3", "国家标准", LocalDateTime.now().minusDays(30));
        addDemo("水", "0.168", "kgCO2/t", "国家标准", LocalDateTime.now().minusDays(30));
        addDemo("蒸汽", "0.11", "kgCO2/kg", "默认", LocalDateTime.now().minusDays(60));
    }

    private static void addDemo(String type, String factor, String unit, String source, LocalDateTime time) {
        Map<String, Object> m = new HashMap<>();
        m.put("id", ID_SEQ++);
        m.put("energyType", type);
        m.put("factorValue", factor);
        m.put("unit", unit);
        m.put("dataSource", source);
        m.put("effectiveDate", time.toString().substring(0, 10));
        m.put("remark", "");
        m.put("createTime", time.toString());
        DATA.add(m);
    }

    @ApiOperation("分页查询碳排放因子")
    @GetMapping("/list")
    public Result<PageResult<Map<String, Object>>> list(
            @ApiParam("页码") @RequestParam(defaultValue = "1") Long current,
            @ApiParam("每页条数") @RequestParam(defaultValue = "10") Long size) {
        int start = (int)((current - 1) * size);
        int end = Math.min(start + size.intValue(), DATA.size());
        List<Map<String, Object>> list = start < DATA.size() ? DATA.subList(start, end) : new ArrayList<>();
        return Result.ok(PageResult.of(current, size, (long)DATA.size(), list));
    }

    @ApiOperation("新增碳排放因子")
    @PostMapping
    public Result<Void> add(@RequestBody Map<String, Object> dto) {
        Map<String, Object> m = new HashMap<>(dto);
        m.put("id", ID_SEQ++);
        m.put("createTime", LocalDateTime.now().toString());
        DATA.add(0, m);
        return Result.ok();
    }
}
