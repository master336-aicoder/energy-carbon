package com.seatone.monitor.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.seatone.common.result.PageResult;
import com.seatone.common.result.Result;
import com.seatone.monitor.dto.EnergyTypeDTO;
import com.seatone.monitor.dto.EnergyTypeTemplateDTO;
import com.seatone.monitor.entity.EnergyType;
import com.seatone.monitor.entity.EnergyTypeTemplate;
import com.seatone.monitor.service.EnergyTypeService;
import com.seatone.monitor.service.EnergyTypeTemplateService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Api(tags = "能源品种管理")
@RestController
@RequestMapping("/monitor/energyType")
public class EnergyTypeController {

    @Autowired
    private EnergyTypeService energyTypeService;

    @Autowired
    private EnergyTypeTemplateService templateService;

    @ApiOperation("分页查询能源品种列表")
    @GetMapping("/list")
    public Result<PageResult<EnergyTypeDTO>> list(
            @ApiParam("页码") @RequestParam(defaultValue = "1") Long current,
            @ApiParam("每页条数") @RequestParam(defaultValue = "10") Long size,
            @ApiParam("能源名称") @RequestParam(required = false) String typeName,
            @ApiParam("能源类别") @RequestParam(required = false) String typeCategory,
            @ApiParam("状态") @RequestParam(required = false) Integer status) {
        Page<EnergyType> page = new Page<>(current, size);
        LambdaQueryWrapper<EnergyType> wrapper = new LambdaQueryWrapper<>();
        if (typeName != null && !typeName.isEmpty()) {
            wrapper.like(EnergyType::getTypeName, typeName);
        }
        if (typeCategory != null && !typeCategory.isEmpty()) {
            wrapper.eq(EnergyType::getTypeCategory, typeCategory);
        }
        if (status != null) {
            wrapper.eq(EnergyType::getStatus, status);
        }
        wrapper.orderByDesc(EnergyType::getCreateTime);
        Page<EnergyType> resultPage = energyTypeService.page(page, wrapper);
        List<EnergyTypeDTO> records = resultPage.getRecords().stream().map(this::convertToDTO).collect(Collectors.toList());
        return Result.ok(PageResult.of(resultPage.getCurrent(), resultPage.getSize(), resultPage.getTotal(), records));
    }

    @ApiOperation("查询所有能源品种")
    @GetMapping("/all")
    public Result<List<EnergyTypeDTO>> all() {
        LambdaQueryWrapper<EnergyType> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(EnergyType::getStatus, 1);
        List<EnergyTypeDTO> list = energyTypeService.list(wrapper).stream().map(this::convertToDTO).collect(Collectors.toList());
        return Result.ok(list);
    }

    @ApiOperation("根据ID查询能源品种")
    @GetMapping("/{id}")
    public Result<EnergyTypeDTO> getById(@ApiParam("能源品种ID") @PathVariable Long id) {
        EnergyType type = energyTypeService.getById(id);
        if (type == null) {
            return Result.error("能源品种不存在");
        }
        return Result.ok(convertToDTO(type));
    }

    @ApiOperation("新增能源品种")
    @PostMapping
    public Result<Void> add(@RequestBody EnergyTypeDTO dto) {
        EnergyType type = new EnergyType();
        BeanUtils.copyProperties(dto, type);
        type.setId(null);
        energyTypeService.save(type);
        return Result.ok();
    }

    @ApiOperation("修改能源品种")
    @PutMapping("/{id}")
    public Result<Void> update(@ApiParam("能源品种ID") @PathVariable Long id, @RequestBody EnergyTypeDTO dto) {
        EnergyType type = new EnergyType();
        BeanUtils.copyProperties(dto, type);
        type.setId(id);
        energyTypeService.updateById(type);
        return Result.ok();
    }

    @ApiOperation("删除能源品种")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@ApiParam("能源品种ID") @PathVariable Long id) {
        energyTypeService.removeById(id);
        return Result.ok();
    }

    @ApiOperation("修改能源品种状态")
    @PutMapping("/{id}/status")
    public Result<Void> changeStatus(@ApiParam("能源品种ID") @PathVariable Long id,
                                      @ApiParam("状态") @RequestParam Integer status) {
        EnergyType type = new EnergyType();
        type.setId(id);
        type.setStatus(status);
        energyTypeService.updateById(type);
        return Result.ok();
    }

    // ==================== 模板管理 ====================

    @ApiOperation("分页查询能源品种模板列表")
    @GetMapping("/template/list")
    public Result<PageResult<EnergyTypeTemplateDTO>> templateList(
            @ApiParam("页码") @RequestParam(defaultValue = "1") Long current,
            @ApiParam("每页条数") @RequestParam(defaultValue = "10") Long size,
            @ApiParam("模板名称") @RequestParam(required = false) String templateName) {
        Page<EnergyTypeTemplate> page = new Page<>(current, size);
        LambdaQueryWrapper<EnergyTypeTemplate> wrapper = new LambdaQueryWrapper<>();
        if (templateName != null && !templateName.isEmpty()) {
            wrapper.like(EnergyTypeTemplate::getTemplateName, templateName);
        }
        wrapper.orderByDesc(EnergyTypeTemplate::getCreateTime);
        Page<EnergyTypeTemplate> resultPage = templateService.page(page, wrapper);
        List<EnergyTypeTemplateDTO> records = resultPage.getRecords().stream().map(this::convertToTemplateDTO).collect(Collectors.toList());
        return Result.ok(PageResult.of(resultPage.getCurrent(), resultPage.getSize(), resultPage.getTotal(), records));
    }

    @ApiOperation("根据ID查询能源品种模板")
    @GetMapping("/template/{id}")
    public Result<EnergyTypeTemplateDTO> getTemplateById(@ApiParam("模板ID") @PathVariable Long id) {
        EnergyTypeTemplate template = templateService.getById(id);
        if (template == null) {
            return Result.error("模板不存在");
        }
        return Result.ok(convertToTemplateDTO(template));
    }

    @ApiOperation("新增能源品种模板")
    @PostMapping("/template")
    public Result<Void> addTemplate(@RequestBody EnergyTypeTemplateDTO dto) {
        EnergyTypeTemplate template = new EnergyTypeTemplate();
        BeanUtils.copyProperties(dto, template);
        template.setId(null);
        templateService.save(template);
        return Result.ok();
    }

    @ApiOperation("修改能源品种模板")
    @PutMapping("/template/{id}")
    public Result<Void> updateTemplate(@ApiParam("模板ID") @PathVariable Long id, @RequestBody EnergyTypeTemplateDTO dto) {
        EnergyTypeTemplate template = new EnergyTypeTemplate();
        BeanUtils.copyProperties(dto, template);
        template.setId(id);
        templateService.updateById(template);
        return Result.ok();
    }

    @ApiOperation("删除能源品种模板")
    @DeleteMapping("/template/{id}")
    public Result<Void> deleteTemplate(@ApiParam("模板ID") @PathVariable Long id) {
        templateService.removeById(id);
        return Result.ok();
    }

    @ApiOperation("根据模板获取能源品种列表")
    @GetMapping("/template/{id}/types")
    public Result<List<EnergyTypeDTO>> getTypesByTemplate(@ApiParam("模板ID") @PathVariable Long id) {
        EnergyTypeTemplate template = templateService.getById(id);
        if (template == null || template.getTypeIds() == null || template.getTypeIds().isEmpty()) {
            return Result.ok(java.util.Collections.emptyList());
        }
        List<Long> typeIds = Arrays.stream(template.getTypeIds().split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(Long::parseLong)
                .collect(Collectors.toList());
        List<EnergyTypeDTO> list = energyTypeService.listByIds(typeIds).stream()
                .map(this::convertToDTO).collect(Collectors.toList());
        return Result.ok(list);
    }

    private EnergyTypeDTO convertToDTO(EnergyType type) {
        EnergyTypeDTO dto = new EnergyTypeDTO();
        BeanUtils.copyProperties(type, dto);
        return dto;
    }

    private EnergyTypeTemplateDTO convertToTemplateDTO(EnergyTypeTemplate template) {
        EnergyTypeTemplateDTO dto = new EnergyTypeTemplateDTO();
        BeanUtils.copyProperties(template, dto);
        return dto;
    }
}
