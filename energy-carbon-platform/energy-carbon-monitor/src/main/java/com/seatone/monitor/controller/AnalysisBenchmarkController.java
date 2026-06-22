package com.seatone.monitor.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.seatone.common.result.PageResult;
import com.seatone.common.result.Result;
import com.seatone.monitor.dto.AnalysisBenchmarkDTO;
import com.seatone.monitor.entity.AnalysisBenchmark;
import com.seatone.monitor.service.AnalysisBenchmarkService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Api(tags = "对标库管理")
@RestController
@RequestMapping("/monitor/analysis/benchmark")
public class AnalysisBenchmarkController {

    @Autowired
    private AnalysisBenchmarkService benchmarkService;

    @ApiOperation("分页查询对标数据")
    @GetMapping("/list")
    public Result<PageResult<AnalysisBenchmarkDTO>> list(
            @ApiParam("页码") @RequestParam(defaultValue = "1") Long current,
            @ApiParam("每页条数") @RequestParam(defaultValue = "10") Long size,
            @ApiParam("对标名称") @RequestParam(required = false) String benchmarkName,
            @ApiParam("所属行业") @RequestParam(required = false) String industry) {
        Page<AnalysisBenchmark> page = new Page<>(current, size);
        LambdaQueryWrapper<AnalysisBenchmark> wrapper = new LambdaQueryWrapper<>();
        if (benchmarkName != null && !benchmarkName.isEmpty()) {
            wrapper.like(AnalysisBenchmark::getBenchmarkName, benchmarkName);
        }
        if (industry != null && !industry.isEmpty()) {
            wrapper.eq(AnalysisBenchmark::getIndustry, industry);
        }
        wrapper.orderByDesc(AnalysisBenchmark::getCreateTime);

        Page<AnalysisBenchmark> resultPage = benchmarkService.page(page, wrapper);
        List<AnalysisBenchmarkDTO> records = resultPage.getRecords().stream()
                .map(this::convertToDTO).collect(Collectors.toList());
        return Result.ok(PageResult.of(resultPage.getCurrent(), resultPage.getSize(), resultPage.getTotal(), records));
    }

    @ApiOperation("查询行业基准")
    @GetMapping("/industry/{industry}")
    public Result<AnalysisBenchmarkDTO> findByIndustry(@ApiParam("所属行业") @PathVariable String industry) {
        AnalysisBenchmark benchmark = benchmarkService.findIndustryBenchmark(industry);
        if (benchmark == null) {
            return Result.error("未找到该行业对标数据");
        }
        return Result.ok(convertToDTO(benchmark));
    }

    @ApiOperation("查询所有生效对标数据")
    @GetMapping("/all")
    public Result<List<AnalysisBenchmarkDTO>> findAll() {
        List<AnalysisBenchmark> list = benchmarkService.findAllActive();
        List<AnalysisBenchmarkDTO> dtos = list.stream().map(this::convertToDTO).collect(Collectors.toList());
        return Result.ok(dtos);
    }

    @ApiOperation("新增对标数据")
    @PostMapping
    public Result<Void> add(@RequestBody AnalysisBenchmarkDTO dto) {
        AnalysisBenchmark entity = new AnalysisBenchmark();
        BeanUtils.copyProperties(dto, entity);
        entity.setId(null);
        benchmarkService.save(entity);
        return Result.ok();
    }

    @ApiOperation("修改对标数据")
    @PutMapping("/{id}")
    public Result<Void> update(@ApiParam("ID") @PathVariable Long id, @RequestBody AnalysisBenchmarkDTO dto) {
        AnalysisBenchmark entity = new AnalysisBenchmark();
        BeanUtils.copyProperties(dto, entity);
        entity.setId(id);
        benchmarkService.updateById(entity);
        return Result.ok();
    }

    @ApiOperation("删除对标数据")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@ApiParam("ID") @PathVariable Long id) {
        benchmarkService.removeById(id);
        return Result.ok();
    }

    private AnalysisBenchmarkDTO convertToDTO(AnalysisBenchmark entity) {
        AnalysisBenchmarkDTO dto = new AnalysisBenchmarkDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }
}