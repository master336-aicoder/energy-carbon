package com.seatone.monitor.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.seatone.common.result.PageResult;
import com.seatone.common.result.Result;
import com.seatone.monitor.dto.CalculatorDTO;
import com.seatone.monitor.entity.ToolkitCalculatorRecord;
import com.seatone.monitor.service.ToolkitCalculatorRecordService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Api(tags = "能碳数据计算器")
@RestController
@RequestMapping("/toolkit/calculator")
public class CalculatorController {

    @Autowired
    private ToolkitCalculatorRecordService calculatorRecordService;

    @ApiOperation("碳排放计算")
    @PostMapping("/carbon")
    public Result<Map<String, Object>> calcCarbon(@RequestBody Map<String, Object> params) {
        double activityData = Double.parseDouble(params.get("activityData").toString());
        double emissionFactor = Double.parseDouble(params.get("emissionFactor").toString());
        double result = activityData * emissionFactor;

        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("activityData", activityData);
        resultMap.put("emissionFactor", emissionFactor);
        resultMap.put("emission", BigDecimal.valueOf(result).setScale(4, RoundingMode.HALF_UP));
        resultMap.put("unit", "tCO2");

        // 保存计算记录
        saveRecord("碳排放计算", "CARBON", params, resultMap);

        return Result.ok(resultMap);
    }

    @ApiOperation("电量计算")
    @PostMapping("/electricity")
    public Result<Map<String, Object>> calcElectricity(@RequestBody Map<String, Object> params) {
        double power = Double.parseDouble(params.get("power").toString());
        double hours = Double.parseDouble(params.get("hours").toString());
        double result = power * hours;

        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("power", power);
        resultMap.put("hours", hours);
        resultMap.put("electricity", BigDecimal.valueOf(result).setScale(2, RoundingMode.HALF_UP));
        resultMap.put("unit", "kWh");

        saveRecord("电量计算", "ELECTRICITY", params, resultMap);

        return Result.ok(resultMap);
    }

    @ApiOperation("成本计算")
    @PostMapping("/cost")
    public Result<Map<String, Object>> calcCost(@RequestBody Map<String, Object> params) {
        double consumption = Double.parseDouble(params.get("consumption").toString());
        double peakPrice = Double.parseDouble(params.get("peakPrice").toString());
        double flatPrice = Double.parseDouble(params.get("flatPrice").toString());
        double valleyPrice = Double.parseDouble(params.get("valleyPrice").toString());
        double peakRatio = Double.parseDouble(params.getOrDefault("peakRatio", "0.3").toString());
        double flatRatio = Double.parseDouble(params.getOrDefault("flatRatio", "0.4").toString());
        double valleyRatio = Double.parseDouble(params.getOrDefault("valleyRatio", "0.3").toString());

        double peakCost = consumption * peakRatio * peakPrice;
        double flatCost = consumption * flatRatio * flatPrice;
        double valleyCost = consumption * valleyRatio * valleyPrice;
        double totalCost = peakCost + flatCost + valleyCost;

        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("consumption", consumption);
        resultMap.put("peakCost", BigDecimal.valueOf(peakCost).setScale(2, RoundingMode.HALF_UP));
        resultMap.put("flatCost", BigDecimal.valueOf(flatCost).setScale(2, RoundingMode.HALF_UP));
        resultMap.put("valleyCost", BigDecimal.valueOf(valleyCost).setScale(2, RoundingMode.HALF_UP));
        resultMap.put("totalCost", BigDecimal.valueOf(totalCost).setScale(2, RoundingMode.HALF_UP));
        resultMap.put("unit", "元");

        saveRecord("成本计算", "COST", params, resultMap);

        return Result.ok(resultMap);
    }

    @ApiOperation("查询计算历史记录")
    @GetMapping("/history")
    public Result<PageResult<CalculatorDTO>> history(
            @ApiParam("页码") @RequestParam(defaultValue = "1") Long current,
            @ApiParam("每页条数") @RequestParam(defaultValue = "10") Long size,
            @ApiParam("计算类型") @RequestParam(required = false) String calcType) {
        Page<ToolkitCalculatorRecord> page = new Page<>(current, size);
        LambdaQueryWrapper<ToolkitCalculatorRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ToolkitCalculatorRecord::getDeleted, 0);
        if (calcType != null && !calcType.isEmpty()) {
            wrapper.eq(ToolkitCalculatorRecord::getCalcType, calcType);
        }
        wrapper.orderByDesc(ToolkitCalculatorRecord::getCreateTime);
        Page<ToolkitCalculatorRecord> resultPage = calculatorRecordService.page(page, wrapper);
        List<CalculatorDTO> records = resultPage.getRecords().stream().map(this::convertToDTO).collect(Collectors.toList());
        return Result.ok(PageResult.of(resultPage.getCurrent(), resultPage.getSize(), resultPage.getTotal(), records));
    }

    @ApiOperation("删除计算记录")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@ApiParam("记录ID") @PathVariable Long id) {
        ToolkitCalculatorRecord record = new ToolkitCalculatorRecord();
        record.setId(id);
        record.setDeleted(1);
        calculatorRecordService.updateById(record);
        return Result.ok();
    }

    private void saveRecord(String calcName, String calcType, Map<String, Object> inputParams, Map<String, Object> result) {
        try {
            ToolkitCalculatorRecord record = new ToolkitCalculatorRecord();
            record.setCalcName(calcName);
            record.setCalcType(calcType);
            record.setInputParamsJson(com.alibaba.fastjson2.JSON.toJSONString(inputParams));
            record.setResultJson(com.alibaba.fastjson2.JSON.toJSONString(result));
            record.setDeleted(0);
            calculatorRecordService.save(record);
        } catch (Exception ignored) {
            // JSON序列化失败不影响计算结果返回
        }
    }

    private CalculatorDTO convertToDTO(ToolkitCalculatorRecord entity) {
        CalculatorDTO dto = new CalculatorDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }
}
