package com.seatone.carbon.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.seatone.carbon.dto.CarbonAccountingModelDTO;
import com.seatone.carbon.entity.CarbonAccountingModel;
import com.seatone.carbon.service.CarbonAccountingModelService;
import com.seatone.common.result.PageResult;
import com.seatone.common.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Api(tags = "碳核算模型")
@RestController
@RequestMapping("/carbon/model")
public class CarbonAccountingModelController {

    @Autowired
    private CarbonAccountingModelService modelService;

    @ApiOperation("分页查询核算模型")
    @GetMapping("/list")
    public Result<PageResult<CarbonAccountingModelDTO>> list(
            @ApiParam("页码") @RequestParam(defaultValue = "1") Long current,
            @ApiParam("每页条数") @RequestParam(defaultValue = "10") Long size,
            @ApiParam("模型名称") @RequestParam(required = false) String modelName) {
        Page<CarbonAccountingModel> page = new Page<>(current, size);
        LambdaQueryWrapper<CarbonAccountingModel> wrapper = new LambdaQueryWrapper<>();
        if (modelName != null && !modelName.isEmpty()) {
            wrapper.like(CarbonAccountingModel::getModelName, modelName);
        }
        wrapper.orderByDesc(CarbonAccountingModel::getCreateTime);
        Page<CarbonAccountingModel> resultPage = modelService.page(page, wrapper);
        List<CarbonAccountingModelDTO> records = resultPage.getRecords().stream()
                .map(this::convertToDTO).collect(Collectors.toList());
        return Result.ok(PageResult.of(resultPage.getCurrent(), resultPage.getSize(), resultPage.getTotal(), records));
    }

    @ApiOperation("获取核算模型详情")
    @GetMapping("/{id}")
    public Result<CarbonAccountingModelDTO> getById(@ApiParam("模型ID") @PathVariable Long id) {
        CarbonAccountingModel entity = modelService.getById(id);
        if (entity == null) {
            return Result.error("模型不存在");
        }
        return Result.ok(convertToDTO(entity));
    }

    @ApiOperation("新增核算模型")
    @PostMapping
    public Result<Void> add(@RequestBody CarbonAccountingModelDTO dto) {
        CarbonAccountingModel entity = new CarbonAccountingModel();
        BeanUtils.copyProperties(dto, entity);
        entity.setId(null);
        modelService.save(entity);
        return Result.ok();
    }

    @ApiOperation("修改核算模型")
    @PutMapping("/{id}")
    public Result<Void> update(@ApiParam("模型ID") @PathVariable Long id, @RequestBody CarbonAccountingModelDTO dto) {
        CarbonAccountingModel entity = new CarbonAccountingModel();
        BeanUtils.copyProperties(dto, entity);
        entity.setId(id);
        modelService.updateById(entity);
        return Result.ok();
    }

    @ApiOperation("删除核算模型")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@ApiParam("模型ID") @PathVariable Long id) {
        modelService.removeById(id);
        return Result.ok();
    }

    private CarbonAccountingModelDTO convertToDTO(CarbonAccountingModel entity) {
        CarbonAccountingModelDTO dto = new CarbonAccountingModelDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }
}