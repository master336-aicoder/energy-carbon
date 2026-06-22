package com.seatone.report.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.seatone.common.result.PageResult;
import com.seatone.common.result.Result;
import com.seatone.report.dto.ReportEnergySavingDTO;
import com.seatone.report.entity.ReportEnergySaving;
import com.seatone.report.service.ReportEnergySavingService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Api(tags = "节能量统计")
@RestController
@RequestMapping("/report/energy/saving")
public class ReportEnergySavingController {

    @Autowired
    private ReportEnergySavingService savingService;

    @ApiOperation("分页查询节能量统计")
    @GetMapping("/list")
    public Result<PageResult<ReportEnergySavingDTO>> list(
            @ApiParam("页码") @RequestParam(defaultValue = "1") Long current,
            @ApiParam("每页条数") @RequestParam(defaultValue = "10") Long size,
            @ApiParam("报表类型") @RequestParam(required = false) String reportType,
            @ApiParam("统计日期") @RequestParam(required = false) LocalDate statDate) {
        Page<ReportEnergySaving> page = new Page<>(current, size);
        LambdaQueryWrapper<ReportEnergySaving> wrapper = new LambdaQueryWrapper<>();
        if (reportType != null && !reportType.isEmpty()) {
            wrapper.eq(ReportEnergySaving::getReportType, reportType);
        }
        if (statDate != null) {
            wrapper.eq(ReportEnergySaving::getStatDate, statDate);
        }
        wrapper.orderByDesc(ReportEnergySaving::getStatDate);
        Page<ReportEnergySaving> resultPage = savingService.page(page, wrapper);
        List<ReportEnergySavingDTO> records = resultPage.getRecords().stream()
                .map(this::convertToDTO).collect(Collectors.toList());
        return Result.ok(PageResult.of(resultPage.getCurrent(), resultPage.getSize(), resultPage.getTotal(), records));
    }

    @ApiOperation("获取节能量详情")
    @GetMapping("/{id}")
    public Result<ReportEnergySavingDTO> getById(@ApiParam("节能ID") @PathVariable Long id) {
        ReportEnergySaving entity = savingService.getById(id);
        if (entity == null) {
            return Result.error("节能量记录不存在");
        }
        return Result.ok(convertToDTO(entity));
    }

    @ApiOperation("新增节能量统计")
    @PostMapping
    public Result<Void> add(@RequestBody ReportEnergySavingDTO dto) {
        ReportEnergySaving entity = new ReportEnergySaving();
        BeanUtils.copyProperties(dto, entity);
        entity.setId(null);
        savingService.save(entity);
        return Result.ok();
    }

    @ApiOperation("删除节能量统计")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@ApiParam("节能ID") @PathVariable Long id) {
        savingService.removeById(id);
        return Result.ok();
    }

    @ApiOperation("按日期范围查询节能量")
    @GetMapping("/range")
    public Result<List<ReportEnergySavingDTO>> getByDateRange(
            @ApiParam("报表类型") @RequestParam String reportType,
            @ApiParam("开始日期") @RequestParam LocalDate startDate,
            @ApiParam("结束日期") @RequestParam LocalDate endDate) {
        List<ReportEnergySaving> list = savingService.list(
                new LambdaQueryWrapper<ReportEnergySaving>()
                        .eq(ReportEnergySaving::getReportType, reportType)
                        .between(ReportEnergySaving::getStatDate, startDate, endDate)
                        .orderByAsc(ReportEnergySaving::getStatDate));
        return Result.ok(list.stream().map(this::convertToDTO).collect(Collectors.toList()));
    }

    private ReportEnergySavingDTO convertToDTO(ReportEnergySaving entity) {
        ReportEnergySavingDTO dto = new ReportEnergySavingDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }
}