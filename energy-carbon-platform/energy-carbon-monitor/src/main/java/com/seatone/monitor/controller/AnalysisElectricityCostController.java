package com.seatone.monitor.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.seatone.common.result.PageResult;
import com.seatone.common.result.Result;
import com.seatone.monitor.dto.AnalysisElectricityCostDTO;
import com.seatone.monitor.entity.AnalysisElectricityCost;
import com.seatone.monitor.service.AnalysisElectricityCostService;
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

@Api(tags = "电费分析")
@RestController
@RequestMapping("/monitor/analysis/electricity")
public class AnalysisElectricityCostController {

    @Autowired
    private AnalysisElectricityCostService costService;

    @ApiOperation("分页查询电费明细")
    @GetMapping("/list")
    public Result<PageResult<AnalysisElectricityCostDTO>> list(
            @ApiParam("页码") @RequestParam(defaultValue = "1") Long current,
            @ApiParam("每页条数") @RequestParam(defaultValue = "10") Long size,
            @ApiParam("计量器具ID") @RequestParam(required = false) Long meterId,
            @ApiParam("开始日期") @RequestParam(required = false) String startDate,
            @ApiParam("结束日期") @RequestParam(required = false) String endDate) {
        Page<AnalysisElectricityCost> page = new Page<>(current, size);
        LambdaQueryWrapper<AnalysisElectricityCost> wrapper = new LambdaQueryWrapper<>();
        if (meterId != null) wrapper.eq(AnalysisElectricityCost::getMeterId, meterId);
        if (startDate != null) wrapper.ge(AnalysisElectricityCost::getStatDate, startDate);
        if (endDate != null) wrapper.le(AnalysisElectricityCost::getStatDate, endDate);
        wrapper.orderByDesc(AnalysisElectricityCost::getStatDate);

        Page<AnalysisElectricityCost> resultPage = costService.page(page, wrapper);
        List<AnalysisElectricityCostDTO> records = resultPage.getRecords().stream()
                .map(this::convertToDTO).collect(Collectors.toList());
        return Result.ok(PageResult.of(resultPage.getCurrent(), resultPage.getSize(), resultPage.getTotal(), records));
    }

    @ApiOperation("电费汇总数据")
    @GetMapping("/summary")
    public Result<Map<String, Object>> summary(
            @ApiParam("计量器具ID") @RequestParam(required = false) Long meterId,
            @ApiParam("开始日期") @RequestParam(required = false) String startDate,
            @ApiParam("结束日期") @RequestParam(required = false) String endDate) {
        Map<String, Object> summary = costService.summaryByPeriod(startDate, endDate, meterId);
        BigDecimal totalCost = costService.sumTotalCost(startDate, endDate, meterId);
        Map<String, Object> result = new HashMap<>();
        result.put("totalCost", totalCost);
        if (summary != null) {
            result.put("peakPower", summary.get("peakPower"));
            result.put("valleyPower", summary.get("valleyPower"));
            result.put("flatPower", summary.get("flatPower"));
            result.put("totalPower", ((BigDecimal) summary.get("peakPower"))
                    .add((BigDecimal) summary.get("valleyPower"))
                    .add((BigDecimal) summary.get("flatPower")));
        }
        return Result.ok(result);
    }

    @ApiOperation("分时电价统计明细（按日期）")
    @GetMapping("/detail")
    public Result<List<Map<String, Object>>> detail(
            @ApiParam("计量器具ID") @RequestParam(required = false) Long meterId,
            @ApiParam("开始日期") @RequestParam(required = false) String startDate,
            @ApiParam("结束日期") @RequestParam(required = false) String endDate) {
        return Result.ok(costService.detailByDate(startDate, endDate, meterId));
    }

    @ApiOperation("新增电费数据")
    @PostMapping
    public Result<Void> add(@RequestBody AnalysisElectricityCostDTO dto) {
        AnalysisElectricityCost entity = new AnalysisElectricityCost();
        BeanUtils.copyProperties(dto, entity);
        entity.setId(null);
        costService.save(entity);
        return Result.ok();
    }

    @ApiOperation("修改电费数据")
    @PutMapping("/{id}")
    public Result<Void> update(@ApiParam("ID") @PathVariable Long id, @RequestBody AnalysisElectricityCostDTO dto) {
        AnalysisElectricityCost entity = new AnalysisElectricityCost();
        BeanUtils.copyProperties(dto, entity);
        entity.setId(id);
        costService.updateById(entity);
        return Result.ok();
    }

    @ApiOperation("删除电费数据")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@ApiParam("ID") @PathVariable Long id) {
        costService.removeById(id);
        return Result.ok();
    }

    private AnalysisElectricityCostDTO convertToDTO(AnalysisElectricityCost entity) {
        AnalysisElectricityCostDTO dto = new AnalysisElectricityCostDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }
}