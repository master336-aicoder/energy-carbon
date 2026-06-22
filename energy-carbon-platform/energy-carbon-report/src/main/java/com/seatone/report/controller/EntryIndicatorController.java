package com.seatone.report.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.seatone.common.result.PageResult;
import com.seatone.common.result.Result;
import com.seatone.report.dto.EntryIndicatorDTO;
import com.seatone.report.entity.EntryIndicator;
import com.seatone.report.service.EntryIndicatorService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Api(tags = "指标录入")
@RestController
@RequestMapping("/entry/indicator")
public class EntryIndicatorController {

    @Autowired
    private EntryIndicatorService indicatorService;

    @ApiOperation("分页查询指标列表")
    @GetMapping("/list")
    public Result<PageResult<EntryIndicatorDTO>> list(
            @ApiParam("页码") @RequestParam(defaultValue = "1") Long current,
            @ApiParam("每页条数") @RequestParam(defaultValue = "10") Long size,
            @ApiParam("指标名称") @RequestParam(required = false) String indicatorName,
            @ApiParam("指标类型") @RequestParam(required = false) String indicatorType,
            @ApiParam("录入日期开始") @RequestParam(required = false) LocalDate startDate,
            @ApiParam("录入日期结束") @RequestParam(required = false) LocalDate endDate) {
        Page<EntryIndicator> page = new Page<>(current, size);
        LambdaQueryWrapper<EntryIndicator> wrapper = new LambdaQueryWrapper<>();
        if (indicatorName != null && !indicatorName.isEmpty()) {
            wrapper.like(EntryIndicator::getIndicatorName, indicatorName);
        }
        if (indicatorType != null && !indicatorType.isEmpty()) {
            wrapper.eq(EntryIndicator::getIndicatorType, indicatorType);
        }
        if (startDate != null) {
            wrapper.ge(EntryIndicator::getEntryDate, startDate);
        }
        if (endDate != null) {
            wrapper.le(EntryIndicator::getEntryDate, endDate);
        }
        wrapper.orderByDesc(EntryIndicator::getCreateTime);
        Page<EntryIndicator> resultPage = indicatorService.page(page, wrapper);
        List<EntryIndicatorDTO> records = resultPage.getRecords().stream()
                .map(this::convertToDTO).collect(Collectors.toList());
        return Result.ok(PageResult.of(resultPage.getCurrent(), resultPage.getSize(), resultPage.getTotal(), records));
    }

    @ApiOperation("获取指标详情")
    @GetMapping("/{id}")
    public Result<EntryIndicatorDTO> getById(@ApiParam("指标ID") @PathVariable Long id) {
        EntryIndicator entity = indicatorService.getById(id);
        if (entity == null) {
            return Result.error("指标不存在");
        }
        return Result.ok(convertToDTO(entity));
    }

    @ApiOperation("新增指标")
    @PostMapping
    public Result<Void> add(@RequestBody EntryIndicatorDTO dto) {
        EntryIndicator entity = new EntryIndicator();
        BeanUtils.copyProperties(dto, entity);
        entity.setId(null);
        indicatorService.save(entity);
        return Result.ok();
    }

    @ApiOperation("修改指标")
    @PutMapping("/{id}")
    public Result<Void> update(@ApiParam("指标ID") @PathVariable Long id, @RequestBody EntryIndicatorDTO dto) {
        EntryIndicator entity = new EntryIndicator();
        BeanUtils.copyProperties(dto, entity);
        entity.setId(id);
        indicatorService.updateById(entity);
        return Result.ok();
    }

    @ApiOperation("删除指标")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@ApiParam("指标ID") @PathVariable Long id) {
        indicatorService.removeById(id);
        return Result.ok();
    }

    @ApiOperation("Excel导入指标")
    @PostMapping("/import")
    public Result<Void> importData(@RequestBody List<EntryIndicatorDTO> dtoList) {
        for (EntryIndicatorDTO dto : dtoList) {
            EntryIndicator entity = new EntryIndicator();
            BeanUtils.copyProperties(dto, entity);
            entity.setId(null);
            indicatorService.save(entity);
        }
        return Result.ok();
    }

    private EntryIndicatorDTO convertToDTO(EntryIndicator entity) {
        EntryIndicatorDTO dto = new EntryIndicatorDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }
}