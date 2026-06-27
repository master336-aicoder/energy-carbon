package com.seatone.system.controller;

import com.seatone.common.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/efficiency/energy-flow")
@Api(tags = "能源流向")
public class EnergyFlowController {

    @GetMapping
    @ApiOperation("获取能源流向数据")
    public Result<Map<String, Object>> getEnergyFlow(
            @RequestParam(required = false) Long energyUnitId,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        Map<String, Object> result = new HashMap<>();
        
        List<Map<String, Object>> nodes = new ArrayList<>();
        String[] nodeNames = {"外购电力", "外购天然气", "外购蒸汽", "外购水",
            "配电系统", "锅炉系统", "换热系统",
            "生产线A", "生产线B", "照明系统", "空调系统",
            "产品A", "产品B", "损耗"};
        for (String name : nodeNames) {
            Map<String, Object> node = new HashMap<>();
            node.put("name", name);
            nodes.add(node);
        }
        
        List<Map<String, Object>> links = new ArrayList<>();
        addLink(links, "外购电力", "配电系统", 8000);
        addLink(links, "外购天然气", "锅炉系统", 5000);
        addLink(links, "外购蒸汽", "换热系统", 3000);
        addLink(links, "外购水", "锅炉系统", 1000);
        addLink(links, "配电系统", "生产线A", 3500);
        addLink(links, "配电系统", "生产线B", 2500);
        addLink(links, "配电系统", "照明系统", 800);
        addLink(links, "配电系统", "空调系统", 1200);
        addLink(links, "锅炉系统", "生产线A", 3000);
        addLink(links, "锅炉系统", "生产线B", 2000);
        addLink(links, "锅炉系统", "损耗", 1000);
        addLink(links, "换热系统", "生产线A", 1500);
        addLink(links, "换热系统", "生产线B", 1000);
        addLink(links, "换热系统", "损耗", 500);
        addLink(links, "生产线A", "产品A", 6000);
        addLink(links, "生产线A", "损耗", 2000);
        addLink(links, "生产线B", "产品B", 4000);
        addLink(links, "生产线B", "损耗", 1500);
        addLink(links, "照明系统", "损耗", 800);
        addLink(links, "空调系统", "损耗", 1200);
        
        result.put("nodes", nodes);
        result.put("links", links);
        return Result.ok(result);
    }
    
    private void addLink(List<Map<String, Object>> links, String source, String target, int value) {
        Map<String, Object> link = new HashMap<>();
        link.put("source", source);
        link.put("target", target);
        link.put("value", value);
        links.add(link);
    }
}
