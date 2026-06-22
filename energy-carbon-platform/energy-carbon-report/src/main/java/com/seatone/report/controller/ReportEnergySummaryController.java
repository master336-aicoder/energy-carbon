package com.seatone.report.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.seatone.common.result.PageResult;
import com.seatone.common.result.Result;
import com.seatone.report.dto.ReportEnergySummaryDTO;
import com.seatone.report.entity.ReportEnergySummary;
import com.seatone.report.service.ReportEnergySummaryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Api(tags = "能耗统计报表")
@RestController
@RequestMapping("/report/energy/summary")
public class ReportEnergySummaryController {

    @Autowired
    private ReportEnergySummaryService summaryService;

    @ApiOperation("分页查询能耗统计")
    @GetMapping("/list")
    public Result<PageResult<ReportEnergySummaryDTO>> list(
            @ApiParam("页码") @RequestParam(defaultValue = "1") Long current,
            @ApiParam("每页条数") @RequestParam(defaultValue = "10") Long size,
            @ApiParam("报表类型(DAILY/MONTHLY/YEARLY)") @RequestParam(required = false) String reportType,
            @ApiParam("统计日期") @RequestParam(required = false) LocalDate statDate,
            @ApiParam("能源类型ID") @RequestParam(required = false) Long energyTypeId) {
        Page<ReportEnergySummary> page = new Page<>(current, size);
        LambdaQueryWrapper<ReportEnergySummary> wrapper = new LambdaQueryWrapper<>();
        if (reportType != null && !reportType.isEmpty()) {
            wrapper.eq(ReportEnergySummary::getReportType, reportType);
        }
        if (statDate != null) {
            wrapper.eq(ReportEnergySummary::getStatDate, statDate);
        }
        if (energyTypeId != null) {
            wrapper.eq(ReportEnergySummary::getEnergyTypeId, energyTypeId);
        }
        wrapper.orderByDesc(ReportEnergySummary::getStatDate);
        Page<ReportEnergySummary> resultPage = summaryService.page(page, wrapper);
        List<ReportEnergySummaryDTO> records = resultPage.getRecords().stream()
                .map(this::convertToDTO).collect(Collectors.toList());
        return Result.ok(PageResult.of(resultPage.getCurrent(), resultPage.getSize(), resultPage.getTotal(), records));
    }

    @ApiOperation("按日期范围查询能耗统计")
    @GetMapping("/range")
    public Result<List<ReportEnergySummaryDTO>> getByDateRange(
            @ApiParam("报表类型") @RequestParam String reportType,
            @ApiParam("开始日期") @RequestParam LocalDate startDate,
            @ApiParam("结束日期") @RequestParam LocalDate endDate) {
        List<ReportEnergySummary> list = summaryService.list(
                new LambdaQueryWrapper<ReportEnergySummary>()
                        .eq(ReportEnergySummary::getReportType, reportType)
                        .between(ReportEnergySummary::getStatDate, startDate, endDate)
                        .orderByAsc(ReportEnergySummary::getStatDate));
        return Result.ok(list.stream().map(this::convertToDTO).collect(Collectors.toList()));
    }

    @ApiOperation("新增能耗统计")
    @PostMapping
    public Result<Void> add(@RequestBody ReportEnergySummaryDTO dto) {
        ReportEnergySummary entity = new ReportEnergySummary();
        BeanUtils.copyProperties(dto, entity);
        entity.setId(null);
        summaryService.save(entity);
        return Result.ok();
    }

    @ApiOperation("删除能耗统计")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@ApiParam("统计ID") @PathVariable Long id) {
        summaryService.removeById(id);
        return Result.ok();
    }

    @ApiOperation("导出能耗统计CSV")
    @GetMapping("/export")
    public void export(HttpServletResponse response,
                       @ApiParam("报表类型") @RequestParam(required = false) String reportType,
                       @ApiParam("开始日期") @RequestParam(required = false) LocalDate startDate,
                       @ApiParam("结束日期") @RequestParam(required = false) LocalDate endDate) throws IOException {
        LambdaQueryWrapper<ReportEnergySummary> wrapper = new LambdaQueryWrapper<>();
        if (reportType != null && !reportType.isEmpty()) {
            wrapper.eq(ReportEnergySummary::getReportType, reportType);
        }
        if (startDate != null && endDate != null) {
            wrapper.between(ReportEnergySummary::getStatDate, startDate, endDate);
        }
        List<ReportEnergySummary> list = summaryService.list(wrapper);

        response.setContentType("text/csv;charset=UTF-8");
        response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode("能耗统计.csv", StandardCharsets.UTF_8.name()));
        OutputStream out = response.getOutputStream();
        out.write(new byte[]{(byte) 0xEF, (byte) 0xBB, (byte) 0xBF});
        out.write("报表类型,统计日期,能源类型ID,消耗量,消耗单位,成本,成本单位\n".getBytes(StandardCharsets.UTF_8));
        for (ReportEnergySummary s : list) {
            String line = String.format("%s,%s,%s,%s,%s,%s,%s\n",
                    s.getReportType(), s.getStatDate(), s.getEnergyTypeId(),
                    s.getConsumption(), s.getConsumptionUnit(), s.getCost(), s.getCostUnit());
            out.write(line.getBytes(StandardCharsets.UTF_8));
        }
        out.flush();
    }

    private ReportEnergySummaryDTO convertToDTO(ReportEnergySummary entity) {
        ReportEnergySummaryDTO dto = new ReportEnergySummaryDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }
}