package com.seatone.monitor.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.seatone.common.result.PageResult;
import com.seatone.common.result.Result;
import com.seatone.monitor.dto.AnalysisEnergyBudgetDTO;
import com.seatone.monitor.entity.AnalysisEnergyBudget;
import com.seatone.monitor.service.AnalysisEnergyBudgetService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Api(tags = "能碳预算管理")
@RestController
@RequestMapping("/monitor/analysis/budget")
public class AnalysisEnergyBudgetController {

    @Autowired
    private AnalysisEnergyBudgetService budgetService;

    @ApiOperation("分页查询预算列表")
    @GetMapping("/list")
    public Result<PageResult<AnalysisEnergyBudgetDTO>> list(
            @ApiParam("页码") @RequestParam(defaultValue = "1") Long current,
            @ApiParam("每页条数") @RequestParam(defaultValue = "10") Long size,
            @ApiParam("用能单元ID") @RequestParam(required = false) Long energyUnitId,
            @ApiParam("预算年度") @RequestParam(required = false) Integer budgetYear,
            @ApiParam("预算类型(ENERGY/CARBON)") @RequestParam(required = false) String budgetType) {
        Page<AnalysisEnergyBudget> page = new Page<>(current, size);
        LambdaQueryWrapper<AnalysisEnergyBudget> wrapper = new LambdaQueryWrapper<>();
        if (energyUnitId != null) wrapper.eq(AnalysisEnergyBudget::getEnergyUnitId, energyUnitId);
        if (budgetYear != null) wrapper.eq(AnalysisEnergyBudget::getBudgetYear, budgetYear);
        if (budgetType != null) wrapper.eq(AnalysisEnergyBudget::getBudgetType, budgetType);
        wrapper.orderByDesc(AnalysisEnergyBudget::getBudgetYear).orderByAsc(AnalysisEnergyBudget::getBudgetType);

        Page<AnalysisEnergyBudget> resultPage = budgetService.page(page, wrapper);
        List<AnalysisEnergyBudgetDTO> records = resultPage.getRecords().stream()
                .map(this::convertToDTO).collect(Collectors.toList());
        return Result.ok(PageResult.of(resultPage.getCurrent(), resultPage.getSize(), resultPage.getTotal(), records));
    }

    @ApiOperation("按年度汇总预算执行情况")
    @GetMapping("/summary/year")
    public Result<List<Map<String, Object>>> summaryByYear(
            @ApiParam("用能单元ID") @RequestParam Long energyUnitId) {
        return Result.ok(budgetService.summaryByYear(energyUnitId));
    }

    @ApiOperation("按年度查询预算列表")
    @GetMapping("/year/{year}")
    public Result<List<AnalysisEnergyBudgetDTO>> listByYear(
            @ApiParam("用能单元ID") @RequestParam Long energyUnitId,
            @ApiParam("预算年度") @PathVariable Integer year) {
        List<AnalysisEnergyBudget> list = budgetService.listByYear(energyUnitId, year);
        List<AnalysisEnergyBudgetDTO> dtos = list.stream().map(this::convertToDTO).collect(Collectors.toList());
        return Result.ok(dtos);
    }

    @ApiOperation("新增预算")
    @PostMapping
    public Result<Void> add(@RequestBody AnalysisEnergyBudgetDTO dto) {
        AnalysisEnergyBudget entity = new AnalysisEnergyBudget();
        BeanUtils.copyProperties(dto, entity);
        entity.setId(null);
        budgetService.save(entity);
        return Result.ok();
    }

    @ApiOperation("修改预算")
    @PutMapping("/{id}")
    public Result<Void> update(@ApiParam("ID") @PathVariable Long id, @RequestBody AnalysisEnergyBudgetDTO dto) {
        AnalysisEnergyBudget entity = new AnalysisEnergyBudget();
        BeanUtils.copyProperties(dto, entity);
        entity.setId(id);
        budgetService.updateById(entity);
        return Result.ok();
    }

    @ApiOperation("删除预算")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@ApiParam("ID") @PathVariable Long id) {
        budgetService.removeById(id);
        return Result.ok();
    }

    @ApiOperation("更新预算完成率")
    @PutMapping("/{id}/completion")
    public Result<Void> updateCompletion(@ApiParam("ID") @PathVariable Long id) {
        budgetService.updateCompletionRate(id);
        return Result.ok();
    }

    @ApiOperation("批量更新预算完成率")
    @PutMapping("/completion/batch")
    public Result<Void> batchUpdateCompletion(
            @ApiParam("用能单元ID") @RequestParam Long energyUnitId,
            @ApiParam("预算年度") @RequestParam Integer year) {
        budgetService.batchUpdateCompletionRate(energyUnitId, year);
        return Result.ok();
    }

    private AnalysisEnergyBudgetDTO convertToDTO(AnalysisEnergyBudget entity) {
        AnalysisEnergyBudgetDTO dto = new AnalysisEnergyBudgetDTO();
        BeanUtils.copyProperties(entity, dto);
        // 设置预算类型名称
        if ("ENERGY".equals(entity.getBudgetType())) {
            dto.setBudgetTypeName("能耗预算");
        } else if ("CARBON".equals(entity.getBudgetType())) {
            dto.setBudgetTypeName("碳排放预算");
        }
        return dto;
    }
}