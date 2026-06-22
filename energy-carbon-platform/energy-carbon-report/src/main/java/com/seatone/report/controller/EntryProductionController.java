package com.seatone.report.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.seatone.common.result.PageResult;
import com.seatone.common.result.Result;
import com.seatone.report.dto.EntryProductionDTO;
import com.seatone.report.entity.EntryProduction;
import com.seatone.report.service.EntryProductionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Api(tags = "产量录入")
@RestController
@RequestMapping("/entry/production")
public class EntryProductionController {

    @Autowired
    private EntryProductionService productionService;

    @ApiOperation("分页查询产量记录")
    @GetMapping("/list")
    public Result<PageResult<EntryProductionDTO>> list(
            @ApiParam("页码") @RequestParam(defaultValue = "1") Long current,
            @ApiParam("每页条数") @RequestParam(defaultValue = "10") Long size,
            @ApiParam("产品类型ID") @RequestParam(required = false) Long productTypeId,
            @ApiParam("批次号") @RequestParam(required = false) String batchNumber,
            @ApiParam("生产日期开始") @RequestParam(required = false) LocalDate startDate,
            @ApiParam("生产日期结束") @RequestParam(required = false) LocalDate endDate) {
        Page<EntryProduction> page = new Page<>(current, size);
        LambdaQueryWrapper<EntryProduction> wrapper = new LambdaQueryWrapper<>();
        if (productTypeId != null) {
            wrapper.eq(EntryProduction::getProductTypeId, productTypeId);
        }
        if (batchNumber != null && !batchNumber.isEmpty()) {
            wrapper.like(EntryProduction::getBatchNumber, batchNumber);
        }
        if (startDate != null) {
            wrapper.ge(EntryProduction::getProductionDate, startDate);
        }
        if (endDate != null) {
            wrapper.le(EntryProduction::getProductionDate, endDate);
        }
        wrapper.orderByDesc(EntryProduction::getProductionDate);
        Page<EntryProduction> resultPage = productionService.page(page, wrapper);
        List<EntryProductionDTO> records = resultPage.getRecords().stream()
                .map(this::convertToDTO).collect(Collectors.toList());
        return Result.ok(PageResult.of(resultPage.getCurrent(), resultPage.getSize(), resultPage.getTotal(), records));
    }

    @ApiOperation("获取产量详情")
    @GetMapping("/{id}")
    public Result<EntryProductionDTO> getById(@ApiParam("产量ID") @PathVariable Long id) {
        EntryProduction entity = productionService.getById(id);
        if (entity == null) {
            return Result.error("产量记录不存在");
        }
        return Result.ok(convertToDTO(entity));
    }

    @ApiOperation("新增产量记录")
    @PostMapping
    public Result<Void> add(@RequestBody EntryProductionDTO dto) {
        EntryProduction entity = new EntryProduction();
        BeanUtils.copyProperties(dto, entity);
        entity.setId(null);
        productionService.save(entity);
        return Result.ok();
    }

    @ApiOperation("修改产量记录")
    @PutMapping("/{id}")
    public Result<Void> update(@ApiParam("产量ID") @PathVariable Long id, @RequestBody EntryProductionDTO dto) {
        EntryProduction entity = new EntryProduction();
        BeanUtils.copyProperties(dto, entity);
        entity.setId(id);
        productionService.updateById(entity);
        return Result.ok();
    }

    @ApiOperation("删除产量记录")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@ApiParam("产量ID") @PathVariable Long id) {
        productionService.removeById(id);
        return Result.ok();
    }

    @ApiOperation("按日期范围查询产量")
    @GetMapping("/range")
    public Result<List<EntryProductionDTO>> getByDateRange(
            @ApiParam("开始日期") @RequestParam LocalDate startDate,
            @ApiParam("结束日期") @RequestParam LocalDate endDate) {
        List<EntryProduction> list = productionService.list(
                new LambdaQueryWrapper<EntryProduction>()
                        .between(EntryProduction::getProductionDate, startDate, endDate)
                        .orderByAsc(EntryProduction::getProductionDate));
        return Result.ok(list.stream().map(this::convertToDTO).collect(Collectors.toList()));
    }

    private EntryProductionDTO convertToDTO(EntryProduction entity) {
        EntryProductionDTO dto = new EntryProductionDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }
}