package com.seatone.monitor.controller;

import com.seatone.common.result.Result;
import com.seatone.common.result.PageResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Api(tags = "能源采购管理")
@RestController
@RequestMapping("/monitor/energy-purchase")
public class EnergyPurchaseController {

    private static final List<Map<String, Object>> DEMO_DATA = new ArrayList<>();
    private static long ID_SEQ = 1;

    static {
        addDemo("电力", new BigDecimal("50000"), new BigDecimal("0.85"), new BigDecimal("42500"), "国家电网", LocalDateTime.now().minusDays(1));
        addDemo("天然气", new BigDecimal("1200"), new BigDecimal("3.2"), new BigDecimal("3840"), "华润燃气", LocalDateTime.now().minusDays(2));
        addDemo("水", new BigDecimal("800"), new BigDecimal("4.5"), new BigDecimal("3600"), "自来水公司", LocalDateTime.now().minusDays(3));
    }

    private static void addDemo(String type, BigDecimal quantity, BigDecimal price, BigDecimal amount, String supplier, LocalDateTime time) {
        Map<String, Object> m = new HashMap<>();
        m.put("id", ID_SEQ++);
        m.put("energyType", type);
        m.put("quantity", quantity);
        m.put("unitPrice", price);
        m.put("totalAmount", amount);
        m.put("supplier", supplier);
        m.put("purchaseDate", time.toString().substring(0, 10));
        m.put("remark", "");
        m.put("createTime", time.toString());
        DEMO_DATA.add(m);
    }

    @ApiOperation("分页查询能源采购记录")
    @GetMapping("/list")
    public Result<PageResult<Map<String, Object>>> list(
            @ApiParam("页码") @RequestParam(defaultValue = "1") Long current,
            @ApiParam("每页条数") @RequestParam(defaultValue = "10") Long size) {
        int start = (int)((current - 1) * size);
        int end = Math.min(start + size.intValue(), DEMO_DATA.size());
        List<Map<String, Object>> list = start < DEMO_DATA.size() ? DEMO_DATA.subList(start, end) : new ArrayList<>();
        return Result.ok(PageResult.of(current, size, (long)DEMO_DATA.size(), list));
    }

    @ApiOperation("新增能源采购记录")
    @PostMapping
    public Result<Void> add(@RequestBody Map<String, Object> dto) {
        Map<String, Object> m = new HashMap<>(dto);
        m.put("id", ID_SEQ++);
        m.put("createTime", LocalDateTime.now().toString());
        if (!m.containsKey("totalAmount") && m.containsKey("quantity") && m.containsKey("unitPrice")) {
            BigDecimal q = new BigDecimal(m.get("quantity").toString());
            BigDecimal p = new BigDecimal(m.get("unitPrice").toString());
            m.put("totalAmount", q.multiply(p));
        }
        DEMO_DATA.add(0, m);
        return Result.ok();
    }
}
