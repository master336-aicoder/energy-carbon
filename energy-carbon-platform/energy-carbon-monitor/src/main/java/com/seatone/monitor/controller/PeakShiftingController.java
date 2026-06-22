package com.seatone.monitor.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.seatone.common.result.PageResult;
import com.seatone.common.result.Result;
import com.seatone.monitor.dto.PeakShiftingConfigDTO;
import com.seatone.monitor.dto.PeakShiftingDTO;
import com.seatone.monitor.entity.ToolkitPeakShifting;
import com.seatone.monitor.entity.ToolkitPeakShiftingConfig;
import com.seatone.monitor.service.ToolkitPeakShiftingConfigService;
import com.seatone.monitor.service.ToolkitPeakShiftingService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Api(tags = "错峰排产管理")
@RestController
@RequestMapping("/toolkit/peak-shifting")
public class PeakShiftingController {

    @Autowired
    private ToolkitPeakShiftingService peakShiftingService;

    @Autowired
    private ToolkitPeakShiftingConfigService configService;

    @ApiOperation("分页查询排产计划列表")
    @GetMapping("/list")
    public Result<PageResult<PeakShiftingDTO>> list(
            @ApiParam("页码") @RequestParam(defaultValue = "1") Long current,
            @ApiParam("每页条数") @RequestParam(defaultValue = "10") Long size,
            @ApiParam("产线名称") @RequestParam(required = false) String productionLine,
            @ApiParam("开始日期") @RequestParam(required = false) String startDate,
            @ApiParam("结束日期") @RequestParam(required = false) String endDate) {
        Page<ToolkitPeakShifting> page = new Page<>(current, size);
        LambdaQueryWrapper<ToolkitPeakShifting> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ToolkitPeakShifting::getDeleted, 0);
        if (productionLine != null && !productionLine.isEmpty()) {
            wrapper.eq(ToolkitPeakShifting::getProductionLine, productionLine);
        }
        if (startDate != null && !startDate.isEmpty()) {
            wrapper.ge(ToolkitPeakShifting::getPlanDate, java.time.LocalDate.parse(startDate));
        }
        if (endDate != null && !endDate.isEmpty()) {
            wrapper.le(ToolkitPeakShifting::getPlanDate, java.time.LocalDate.parse(endDate));
        }
        wrapper.orderByDesc(ToolkitPeakShifting::getPlanDate);
        Page<ToolkitPeakShifting> resultPage = peakShiftingService.page(page, wrapper);
        List<PeakShiftingDTO> records = resultPage.getRecords().stream().map(this::convertToDTO).collect(Collectors.toList());
        return Result.ok(PageResult.of(resultPage.getCurrent(), resultPage.getSize(), resultPage.getTotal(), records));
    }

    @ApiOperation("新增排产计划")
    @PostMapping
    public Result<Void> add(@RequestBody ToolkitPeakShifting plan) {
        plan.setDeleted(0);
        peakShiftingService.save(plan);
        return Result.ok();
    }

    @ApiOperation("修改排产计划")
    @PutMapping("/{id}")
    public Result<Void> update(@ApiParam("计划ID") @PathVariable Long id, @RequestBody ToolkitPeakShifting plan) {
        plan.setId(id);
        peakShiftingService.updateById(plan);
        return Result.ok();
    }

    @ApiOperation("删除排产计划")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@ApiParam("计划ID") @PathVariable Long id) {
        ToolkitPeakShifting plan = new ToolkitPeakShifting();
        plan.setId(id);
        plan.setDeleted(1);
        peakShiftingService.updateById(plan);
        return Result.ok();
    }

    @ApiOperation("获取排产配置列表")
    @GetMapping("/config/list")
    public Result<List<PeakShiftingConfigDTO>> configList() {
        LambdaQueryWrapper<ToolkitPeakShiftingConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ToolkitPeakShiftingConfig::getDeleted, 0);
        wrapper.orderByAsc(ToolkitPeakShiftingConfig::getPriority);
        List<ToolkitPeakShiftingConfig> configs = configService.list(wrapper);
        List<PeakShiftingConfigDTO> records = configs.stream().map(this::convertConfigToDTO).collect(Collectors.toList());
        return Result.ok(records);
    }

    @ApiOperation("新增排产配置")
    @PostMapping("/config")
    public Result<Void> addConfig(@RequestBody ToolkitPeakShiftingConfig config) {
        config.setDeleted(0);
        configService.save(config);
        return Result.ok();
    }

    @ApiOperation("修改排产配置")
    @PutMapping("/config/{id}")
    public Result<Void> updateConfig(@ApiParam("配置ID") @PathVariable Long id, @RequestBody ToolkitPeakShiftingConfig config) {
        config.setId(id);
        configService.updateById(config);
        return Result.ok();
    }

    @ApiOperation("删除排产配置")
    @DeleteMapping("/config/{id}")
    public Result<Void> deleteConfig(@ApiParam("配置ID") @PathVariable Long id) {
        ToolkitPeakShiftingConfig config = new ToolkitPeakShiftingConfig();
        config.setId(id);
        config.setDeleted(1);
        configService.updateById(config);
        return Result.ok();
    }

    @ApiOperation("智能排产建议")
    @GetMapping("/suggestion")
    public Result<Map<String, Object>> getSuggestion(
            @ApiParam("产线名称") @RequestParam String productionLine,
            @ApiParam("计划日期") @RequestParam String planDate) {
        Map<String, Object> suggestion = new HashMap<>();

        // 获取当前生效的排产配置
        LambdaQueryWrapper<ToolkitPeakShiftingConfig> configWrapper = new LambdaQueryWrapper<>();
        configWrapper.eq(ToolkitPeakShiftingConfig::getDeleted, 0);
        configWrapper.eq(ToolkitPeakShiftingConfig::getStatus, 1);
        configWrapper.orderByDesc(ToolkitPeakShiftingConfig::getPriority);
        configWrapper.last("LIMIT 1");
        ToolkitPeakShiftingConfig config = configService.getOne(configWrapper);

        if (config != null) {
            suggestion.put("peakHoursStart", config.getPeakHoursStart());
            suggestion.put("peakHoursEnd", config.getPeakHoursEnd());
            suggestion.put("valleyHoursStart", config.getValleyHoursStart());
            suggestion.put("valleyHoursEnd", config.getValleyHoursEnd());
            suggestion.put("maxPower", config.getMaxPower());
            suggestion.put("suggestion", "建议将高能耗工序安排在谷时段(" + config.getValleyHoursStart() + "-" + config.getValleyHoursEnd() + ")进行，避开峰时段(" + config.getPeakHoursStart() + "-" + config.getPeakHoursEnd() + ")");
        } else {
            suggestion.put("suggestion", "暂无排产配置，请先配置峰谷时段");
        }

        return Result.ok(suggestion);
    }

    private PeakShiftingDTO convertToDTO(ToolkitPeakShifting entity) {
        PeakShiftingDTO dto = new PeakShiftingDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }

    private PeakShiftingConfigDTO convertConfigToDTO(ToolkitPeakShiftingConfig entity) {
        PeakShiftingConfigDTO dto = new PeakShiftingConfigDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }
}
