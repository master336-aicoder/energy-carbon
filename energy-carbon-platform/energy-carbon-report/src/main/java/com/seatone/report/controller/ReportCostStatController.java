package com.seatone.report.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.seatone.common.result.PageResult;
import com.seatone.common.result.Result;
import com.seatone.report.dto.ReportCostStatDTO;
import com.seatone.report.entity.ReportCostStat;
import com.seatone.report.service.ReportCostStatService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Api(tags = "用能成本统计")
@RestController
@RequestMapping("/report/cost")
public class ReportCostStatController {

    @Autowired
    private ReportCostStatService costStatService;

    @ApiOperation("分页查询成本统计")
    @GetMapping("/list")
    public Result<PageResult<ReportCostStatDTO>> list(
            @ApiParam("页码") @RequestParam(defaultValue = "1") Long current,
            @ApiParam("每页条数") @RequestParam(defaultValue = "10") Long size,
            @ApiParam("报表类型") @RequestParam(required = false) String reportType,
            @ApiParam("统计日期") @RequestParam(required = false) LocalDate statDate,
            @ApiParam("能源类型ID") @RequestParam(required = false) Long energyTypeId) {
        Page<ReportCostStat> page = new Page<>(current, size);
        LambdaQueryWrapper<ReportCostStat> wrapper = new LambdaQueryWrapper<>();
        if (reportType != null && !reportType.isEmpty()) {
            wrapper.eq(ReportCostStat::getReportType, reportType);
        }
        if (statDate != null) {
            wrapper.eq(ReportCostStat::getStatDate, statDate);
        }
        if (energyTypeId != null) {
            wrapper.eq(ReportCostStat::getEnergyTypeId, energyTypeId);
        }
        wrapper.orderByDesc(ReportCostStat::getStatDate);
        Page<ReportCostStat> resultPage = costStatService.page(page, wrapper);
        List<ReportCostStatDTO> records = resultPage.getRecords().stream()
                .map(this::convertToDTO).collect(Collectors.toList());
        return Result.ok(PageResult.of(resultPage.getCurrent(), resultPage.getSize(), resultPage.getTotal(), records));
    }

    @ApiOperation("获取成本统计详情")
    @GetMapping("/{id}")
    public Result<ReportCostStatDTO> getById(@ApiParam("成本ID") @PathVariable Long id) {
        ReportCostStat entity = costStatService.getById(id);
        if (entity == null) {
            return Result.error("成本记录不存在");
        }
        return Result.ok(convertToDTO(entity));
    }

    @ApiOperation("新增成本统计")
    @PostMapping
    public Result<Void> add(@RequestBody ReportCostStatDTO dto) {
        ReportCostStat entity = new ReportCostStat();
        BeanUtils.copyProperties(dto, entity);
        entity.setId(null);
        costStatService.save(entity);
        return Result.ok();
    }

    @ApiOperation("删除成本统计")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@ApiParam("成本ID") @PathVariable Long id) {
        costStatService.removeById(id);
        return Result.ok();
    }

    @ApiOperation("按日期范围查询成本")
    @GetMapping("/range")
    public Result<List<ReportCostStatDTO>> getByDateRange(
            @ApiParam("报表类型") @RequestParam String reportType,
            @ApiParam("开始日期") @RequestParam LocalDate startDate,
            @ApiParam("结束日期") @RequestParam LocalDate endDate) {
        List<ReportCostStat> list = costStatService.list(
                new LambdaQueryWrapper<ReportCostStat>()
                        .eq(ReportCostStat::getReportType, reportType)
                        .between(ReportCostStat::getStatDate, startDate, endDate)
                        .orderByAsc(ReportCostStat::getStatDate));
        return Result.ok(list.stream().map(this::convertToDTO).collect(Collectors.toList()));
    }

    private ReportCostStatDTO convertToDTO(ReportCostStat entity) {
        ReportCostStatDTO dto = new ReportCostStatDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }
}