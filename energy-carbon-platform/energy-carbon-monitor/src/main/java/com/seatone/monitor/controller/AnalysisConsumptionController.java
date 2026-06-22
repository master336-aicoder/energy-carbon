package com.seatone.monitor.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.seatone.common.result.PageResult;
import com.seatone.common.result.Result;
import com.seatone.monitor.dto.AnalysisConsumptionDTO;
import com.seatone.monitor.entity.AnalysisConsumption;
import com.seatone.monitor.service.AnalysisConsumptionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Api(tags = "能耗分析")
@RestController
@RequestMapping("/monitor/analysis/consumption")
public class AnalysisConsumptionController {

    @Autowired
    private AnalysisConsumptionService consumptionService;

    @ApiOperation("分页查询能耗数据")
    @GetMapping("/list")
    public Result<PageResult<AnalysisConsumptionDTO>> list(
            @ApiParam("页码") @RequestParam(defaultValue = "1") Long current,
            @ApiParam("每页条数") @RequestParam(defaultValue = "10") Long size,
            @ApiParam("用能单元ID") @RequestParam(required = false) Long energyUnitId,
            @ApiParam("计量器具ID") @RequestParam(required = false) Long meterId,
            @ApiParam("能源品种ID") @RequestParam(required = false) Long energyTypeId,
            @ApiParam("统计周期(DAY/MONTH/YEAR)") @RequestParam(required = false) String statPeriod,
            @ApiParam("开始日期") @RequestParam(required = false) String startDate,
            @ApiParam("结束日期") @RequestParam(required = false) String endDate) {
        Page<AnalysisConsumption> page = new Page<>(current, size);
        LambdaQueryWrapper<AnalysisConsumption> wrapper = new LambdaQueryWrapper<>();
        if (energyUnitId != null) wrapper.eq(AnalysisConsumption::getEnergyUnitId, energyUnitId);
        if (meterId != null) wrapper.eq(AnalysisConsumption::getMeterId, meterId);
        if (energyTypeId != null) wrapper.eq(AnalysisConsumption::getEnergyTypeId, energyTypeId);
        if (statPeriod != null) wrapper.eq(AnalysisConsumption::getStatPeriod, statPeriod);
        if (startDate != null) wrapper.ge(AnalysisConsumption::getStatDate, startDate);
        if (endDate != null) wrapper.le(AnalysisConsumption::getStatDate, endDate);
        wrapper.orderByDesc(AnalysisConsumption::getStatDate);

        Page<AnalysisConsumption> resultPage = consumptionService.page(page, wrapper);
        List<AnalysisConsumptionDTO> records = resultPage.getRecords().stream()
                .map(this::convertToDTO).collect(Collectors.toList());
        return Result.ok(PageResult.of(resultPage.getCurrent(), resultPage.getSize(), resultPage.getTotal(), records));
    }

    @ApiOperation("能耗汇总卡片数据")
    @GetMapping("/summary")
    public Result<Map<String, Object>> summary(
            @ApiParam("用能单元ID") @RequestParam(required = false) Long energyUnitId,
            @ApiParam("统计周期(DAY/MONTH/YEAR)") @RequestParam(defaultValue = "MONTH") String statPeriod,
            @ApiParam("开始日期") @RequestParam(required = false) String startDate,
            @ApiParam("结束日期") @RequestParam(required = false) String endDate,
            @ApiParam("能源品种ID") @RequestParam(required = false) Long energyTypeId) {
        Map<String, Object> result = new HashMap<>();
        BigDecimal total = consumptionService.sumConsumption(energyUnitId, statPeriod, startDate, endDate, energyTypeId);
        BigDecimal samePeriod = consumptionService.getSamePeriodConsumption(energyUnitId, startDate, endDate, energyTypeId);
        BigDecimal prevPeriod = consumptionService.getPreviousPeriodConsumption(energyUnitId, statPeriod, endDate, energyTypeId);

        result.put("totalConsumption", total);
        result.put("samePeriodConsumption", samePeriod);
        result.put("prevPeriodConsumption", prevPeriod);

        // 同比变化率
        if (samePeriod.compareTo(BigDecimal.ZERO) > 0) {
            result.put("yoyRate", total.subtract(samePeriod).divide(samePeriod, 4, BigDecimal.ROUND_HALF_UP)
                    .multiply(new BigDecimal("100")).setScale(2, BigDecimal.ROUND_HALF_UP));
        } else {
            result.put("yoyRate", null);
        }
        // 环比变化率
        if (prevPeriod.compareTo(BigDecimal.ZERO) > 0) {
            result.put("momRate", total.subtract(prevPeriod).divide(prevPeriod, 4, BigDecimal.ROUND_HALF_UP)
                    .multiply(new BigDecimal("100")).setScale(2, BigDecimal.ROUND_HALF_UP));
        } else {
            result.put("momRate", null);
        }

        // 按能源品种占比
        List<Map<String, Object>> typeRatio = consumptionService.groupByEnergyType(energyUnitId, statPeriod, startDate, endDate);
        result.put("typeRatio", typeRatio);

        return Result.ok(result);
    }

    @ApiOperation("能耗趋势数据")
    @GetMapping("/trend")
    public Result<List<Map<String, Object>>> trend(
            @ApiParam("用能单元ID") @RequestParam(required = false) Long energyUnitId,
            @ApiParam("统计周期(DAY/MONTH/YEAR)") @RequestParam(defaultValue = "DAY") String statPeriod,
            @ApiParam("开始日期") @RequestParam(required = false) String startDate,
            @ApiParam("结束日期") @RequestParam(required = false) String endDate,
            @ApiParam("能源品种ID") @RequestParam(required = false) Long energyTypeId) {
        return Result.ok(consumptionService.trendByDate(energyUnitId, statPeriod, startDate, endDate, energyTypeId));
    }

    @ApiOperation("新增能耗数据")
    @PostMapping
    public Result<Void> add(@RequestBody AnalysisConsumptionDTO dto) {
        AnalysisConsumption entity = new AnalysisConsumption();
        BeanUtils.copyProperties(dto, entity);
        entity.setId(null);
        consumptionService.save(entity);
        return Result.ok();
    }

    @ApiOperation("修改能耗数据")
    @PutMapping("/{id}")
    public Result<Void> update(@ApiParam("ID") @PathVariable Long id, @RequestBody AnalysisConsumptionDTO dto) {
        AnalysisConsumption entity = new AnalysisConsumption();
        BeanUtils.copyProperties(dto, entity);
        entity.setId(id);
        consumptionService.updateById(entity);
        return Result.ok();
    }

    @ApiOperation("删除能耗数据")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@ApiParam("ID") @PathVariable Long id) {
        consumptionService.removeById(id);
        return Result.ok();
    }

    private AnalysisConsumptionDTO convertToDTO(AnalysisConsumption entity) {
        AnalysisConsumptionDTO dto = new AnalysisConsumptionDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }
}