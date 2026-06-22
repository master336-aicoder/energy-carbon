package com.seatone.report.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.seatone.common.result.PageResult;
import com.seatone.common.result.Result;
import com.seatone.report.dto.ReportEnergyTotalDTO;
import com.seatone.report.entity.ReportEnergyTotal;
import com.seatone.report.service.ReportEnergyTotalService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Api(tags = "能耗汇总报表")
@RestController
@RequestMapping("/report/energy/total")
public class ReportEnergyTotalController {

    @Autowired
    private ReportEnergyTotalService totalService;

    @ApiOperation("分页查询能耗汇总")
    @GetMapping("/list")
    public Result<PageResult<ReportEnergyTotalDTO>> list(
            @ApiParam("页码") @RequestParam(defaultValue = "1") Long current,
            @ApiParam("每页条数") @RequestParam(defaultValue = "10") Long size,
            @ApiParam("报表类型") @RequestParam(required = false) String reportType,
            @ApiParam("统计日期") @RequestParam(required = false) LocalDate statDate) {
        Page<ReportEnergyTotal> page = new Page<>(current, size);
        LambdaQueryWrapper<ReportEnergyTotal> wrapper = new LambdaQueryWrapper<>();
        if (reportType != null && !reportType.isEmpty()) {
            wrapper.eq(ReportEnergyTotal::getReportType, reportType);
        }
        if (statDate != null) {
            wrapper.eq(ReportEnergyTotal::getStatDate, statDate);
        }
        wrapper.orderByDesc(ReportEnergyTotal::getStatDate);
        Page<ReportEnergyTotal> resultPage = totalService.page(page, wrapper);
        List<ReportEnergyTotalDTO> records = resultPage.getRecords().stream()
                .map(this::convertToDTO).collect(Collectors.toList());
        return Result.ok(PageResult.of(resultPage.getCurrent(), resultPage.getSize(), resultPage.getTotal(), records));
    }

    @ApiOperation("获取能耗汇总详情")
    @GetMapping("/{id}")
    public Result<ReportEnergyTotalDTO> getById(@ApiParam("汇总ID") @PathVariable Long id) {
        ReportEnergyTotal entity = totalService.getById(id);
        if (entity == null) {
            return Result.error("汇总记录不存在");
        }
        return Result.ok(convertToDTO(entity));
    }

    @ApiOperation("新增能耗汇总")
    @PostMapping
    public Result<Void> add(@RequestBody ReportEnergyTotalDTO dto) {
        ReportEnergyTotal entity = new ReportEnergyTotal();
        BeanUtils.copyProperties(dto, entity);
        entity.setId(null);
        totalService.save(entity);
        return Result.ok();
    }

    @ApiOperation("删除能耗汇总")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@ApiParam("汇总ID") @PathVariable Long id) {
        totalService.removeById(id);
        return Result.ok();
    }

    @ApiOperation("按日期范围查询汇总")
    @GetMapping("/range")
    public Result<List<ReportEnergyTotalDTO>> getByDateRange(
            @ApiParam("报表类型") @RequestParam String reportType,
            @ApiParam("开始日期") @RequestParam LocalDate startDate,
            @ApiParam("结束日期") @RequestParam LocalDate endDate) {
        List<ReportEnergyTotal> list = totalService.list(
                new LambdaQueryWrapper<ReportEnergyTotal>()
                        .eq(ReportEnergyTotal::getReportType, reportType)
                        .between(ReportEnergyTotal::getStatDate, startDate, endDate)
                        .orderByAsc(ReportEnergyTotal::getStatDate));
        return Result.ok(list.stream().map(this::convertToDTO).collect(Collectors.toList()));
    }

    private ReportEnergyTotalDTO convertToDTO(ReportEnergyTotal entity) {
        ReportEnergyTotalDTO dto = new ReportEnergyTotalDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }
}