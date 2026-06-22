package com.seatone.report.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.seatone.common.result.PageResult;
import com.seatone.common.result.Result;
import com.seatone.report.dto.BaselineConsumptionDTO;
import com.seatone.report.entity.BaselineConsumption;
import com.seatone.report.service.BaselineConsumptionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Api(tags = "基准能耗")
@RestController
@RequestMapping("/report/baseline")
public class BaselineConsumptionController {

    @Autowired
    private BaselineConsumptionService baselineService;

    @ApiOperation("分页查询基准能耗")
    @GetMapping("/list")
    public Result<PageResult<BaselineConsumptionDTO>> list(
            @ApiParam("页码") @RequestParam(defaultValue = "1") Long current,
            @ApiParam("每页条数") @RequestParam(defaultValue = "10") Long size,
            @ApiParam("基准类型") @RequestParam(required = false) String baselineType) {
        Page<BaselineConsumption> page = new Page<>(current, size);
        LambdaQueryWrapper<BaselineConsumption> wrapper = new LambdaQueryWrapper<>();
        if (baselineType != null && !baselineType.isEmpty()) {
            wrapper.eq(BaselineConsumption::getBaselineType, baselineType);
        }
        wrapper.orderByDesc(BaselineConsumption::getCreateTime);
        Page<BaselineConsumption> resultPage = baselineService.page(page, wrapper);
        List<BaselineConsumptionDTO> records = resultPage.getRecords().stream()
                .map(this::convertToDTO).collect(Collectors.toList());
        return Result.ok(PageResult.of(resultPage.getCurrent(), resultPage.getSize(), resultPage.getTotal(), records));
    }

    @ApiOperation("获取基准能耗详情")
    @GetMapping("/{id}")
    public Result<BaselineConsumptionDTO> getById(@ApiParam("基准ID") @PathVariable Long id) {
        BaselineConsumption entity = baselineService.getById(id);
        if (entity == null) {
            return Result.error("基准能耗不存在");
        }
        return Result.ok(convertToDTO(entity));
    }

    @ApiOperation("新增基准能耗")
    @PostMapping
    public Result<Void> add(@RequestBody BaselineConsumptionDTO dto) {
        BaselineConsumption entity = new BaselineConsumption();
        BeanUtils.copyProperties(dto, entity);
        entity.setId(null);
        baselineService.save(entity);
        return Result.ok();
    }

    @ApiOperation("修改基准能耗")
    @PutMapping("/{id}")
    public Result<Void> update(@ApiParam("基准ID") @PathVariable Long id, @RequestBody BaselineConsumptionDTO dto) {
        BaselineConsumption entity = new BaselineConsumption();
        BeanUtils.copyProperties(dto, entity);
        entity.setId(id);
        baselineService.updateById(entity);
        return Result.ok();
    }

    @ApiOperation("删除基准能耗")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@ApiParam("基准ID") @PathVariable Long id) {
        baselineService.removeById(id);
        return Result.ok();
    }

    private BaselineConsumptionDTO convertToDTO(BaselineConsumption entity) {
        BaselineConsumptionDTO dto = new BaselineConsumptionDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }
}