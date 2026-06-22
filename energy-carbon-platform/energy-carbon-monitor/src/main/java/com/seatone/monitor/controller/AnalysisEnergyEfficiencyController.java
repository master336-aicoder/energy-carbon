package com.seatone.monitor.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.seatone.common.result.PageResult;
import com.seatone.common.result.Result;
import com.seatone.monitor.dto.AnalysisEnergyEfficiencyDTO;
import com.seatone.monitor.entity.AnalysisEnergyEfficiency;
import com.seatone.monitor.service.AnalysisEnergyEfficiencyService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Api(tags = "能效分析")
@RestController
@RequestMapping("/monitor/analysis/efficiency")
public class AnalysisEnergyEfficiencyController {

    @Autowired
    private AnalysisEnergyEfficiencyService efficiencyService;

    @ApiOperation("分页查询能效数据")
    @GetMapping("/list")
    public Result<PageResult<AnalysisEnergyEfficiencyDTO>> list(
            @ApiParam("页码") @RequestParam(defaultValue = "1") Long current,
            @ApiParam("每页条数") @RequestParam(defaultValue = "10") Long size,
            @ApiParam("用能单元ID") @RequestParam(required = false) Long energyUnitId,
            @ApiParam("开始日期") @RequestParam(required = false) String startDate,
            @ApiParam("结束日期") @RequestParam(required = false) String endDate) {
        Page<AnalysisEnergyEfficiency> page = new Page<>(current, size);
        LambdaQueryWrapper<AnalysisEnergyEfficiency> wrapper = new LambdaQueryWrapper<>();
        if (energyUnitId != null) wrapper.eq(AnalysisEnergyEfficiency::getEnergyUnitId, energyUnitId);
        if (startDate != null) wrapper.ge(AnalysisEnergyEfficiency::getStatDate, startDate);
        if (endDate != null) wrapper.le(AnalysisEnergyEfficiency::getStatDate, endDate);
        wrapper.orderByDesc(AnalysisEnergyEfficiency::getStatDate);

        Page<AnalysisEnergyEfficiency> resultPage = efficiencyService.page(page, wrapper);
        List<AnalysisEnergyEfficiencyDTO> records = resultPage.getRecords().stream()
                .map(this::convertToDTO).collect(Collectors.toList());
        return Result.ok(PageResult.of(resultPage.getCurrent(), resultPage.getSize(), resultPage.getTotal(), records));
    }

    @ApiOperation("能效趋势数据")
    @GetMapping("/trend")
    public Result<List<Map<String, Object>>> trend(
            @ApiParam("用能单元ID") @RequestParam Long energyUnitId,
            @ApiParam("开始日期") @RequestParam(required = false) String startDate,
            @ApiParam("结束日期") @RequestParam(required = false) String endDate) {
        return Result.ok(efficiencyService.trendData(energyUnitId, startDate, endDate));
    }

    @ApiOperation("能耗强度计算")
    @GetMapping("/intensity")
    public Result<Map<String, Object>> intensity(
            @ApiParam("用能单元ID") @RequestParam Long energyUnitId,
            @ApiParam("开始日期") @RequestParam(required = false) String startDate,
            @ApiParam("结束日期") @RequestParam(required = false) String endDate) {
        return Result.ok(efficiencyService.calculateIntensity(energyUnitId, startDate, endDate));
    }

    @ApiOperation("对标分析结果")
    @GetMapping("/benchmark")
    public Result<Map<String, Object>> benchmark(
            @ApiParam("用能单元ID") @RequestParam Long energyUnitId,
            @ApiParam("所属行业") @RequestParam String industry,
            @ApiParam("开始日期") @RequestParam(required = false) String startDate,
            @ApiParam("结束日期") @RequestParam(required = false) String endDate) {
        return Result.ok(efficiencyService.benchmarkAnalysis(energyUnitId, industry, startDate, endDate));
    }

    @ApiOperation("新增能效数据")
    @PostMapping
    public Result<Void> add(@RequestBody AnalysisEnergyEfficiencyDTO dto) {
        AnalysisEnergyEfficiency entity = new AnalysisEnergyEfficiency();
        BeanUtils.copyProperties(dto, entity);
        entity.setId(null);
        efficiencyService.save(entity);
        return Result.ok();
    }

    @ApiOperation("修改能效数据")
    @PutMapping("/{id}")
    public Result<Void> update(@ApiParam("ID") @PathVariable Long id, @RequestBody AnalysisEnergyEfficiencyDTO dto) {
        AnalysisEnergyEfficiency entity = new AnalysisEnergyEfficiency();
        BeanUtils.copyProperties(dto, entity);
        entity.setId(id);
        efficiencyService.updateById(entity);
        return Result.ok();
    }

    @ApiOperation("删除能效数据")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@ApiParam("ID") @PathVariable Long id) {
        efficiencyService.removeById(id);
        return Result.ok();
    }

    private AnalysisEnergyEfficiencyDTO convertToDTO(AnalysisEnergyEfficiency entity) {
        AnalysisEnergyEfficiencyDTO dto = new AnalysisEnergyEfficiencyDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }
}