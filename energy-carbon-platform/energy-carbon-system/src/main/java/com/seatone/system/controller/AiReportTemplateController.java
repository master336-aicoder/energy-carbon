package com.seatone.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.seatone.common.result.PageResult;
import com.seatone.common.result.Result;
import com.seatone.system.dto.AiReportTemplateDTO;
import com.seatone.system.entity.AiReportTemplate;
import com.seatone.system.service.AiReportTemplateService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Api(tags = "AI报告模板管理")
@RestController
@RequestMapping("/ai/report-template")
public class AiReportTemplateController {

    @Autowired
    private AiReportTemplateService templateService;

    @ApiOperation("分页查询模板列表")
    @GetMapping("/list")
    public Result<PageResult<AiReportTemplateDTO>> list(
            @ApiParam("页码") @RequestParam(defaultValue = "1") Long current,
            @ApiParam("每页条数") @RequestParam(defaultValue = "10") Long size,
            @ApiParam("报告类型") @RequestParam(required = false) String reportType) {
        Page<AiReportTemplate> page = new Page<>(current, size);
        LambdaQueryWrapper<AiReportTemplate> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AiReportTemplate::getDeleted, 0);
        if (reportType != null && !reportType.isEmpty()) {
            wrapper.eq(AiReportTemplate::getReportType, reportType);
        }
        wrapper.orderByDesc(AiReportTemplate::getCreateTime);
        Page<AiReportTemplate> resultPage = templateService.page(page, wrapper);
        List<AiReportTemplateDTO> records = resultPage.getRecords().stream().map(this::convertToDTO).collect(Collectors.toList());
        return Result.ok(PageResult.of(resultPage.getCurrent(), resultPage.getSize(), resultPage.getTotal(), records));
    }

    @ApiOperation("获取所有模板")
    @GetMapping("/all")
    public Result<List<AiReportTemplateDTO>> all(
            @ApiParam("报告类型") @RequestParam(required = false) String reportType) {
        LambdaQueryWrapper<AiReportTemplate> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AiReportTemplate::getDeleted, 0);
        wrapper.eq(AiReportTemplate::getStatus, 1);
        if (reportType != null && !reportType.isEmpty()) {
            wrapper.eq(AiReportTemplate::getReportType, reportType);
        }
        wrapper.orderByDesc(AiReportTemplate::getCreateTime);
        List<AiReportTemplate> list = templateService.list(wrapper);
        List<AiReportTemplateDTO> records = list.stream().map(this::convertToDTO).collect(Collectors.toList());
        return Result.ok(records);
    }

    @ApiOperation("获取模板详情")
    @GetMapping("/{id}")
    public Result<AiReportTemplateDTO> getById(@ApiParam("模板ID") @PathVariable Long id) {
        AiReportTemplate entity = templateService.getById(id);
        if (entity == null) {
            return Result.error("模板不存在");
        }
        return Result.ok(convertToDTO(entity));
    }

    @ApiOperation("新增模板")
    @PostMapping
    public Result<Void> add(@RequestBody AiReportTemplate template) {
        template.setDeleted(0);
        templateService.save(template);
        return Result.ok();
    }

    @ApiOperation("修改模板")
    @PutMapping("/{id}")
    public Result<Void> update(@ApiParam("模板ID") @PathVariable Long id, @RequestBody AiReportTemplate template) {
        template.setId(id);
        templateService.updateById(template);
        return Result.ok();
    }

    @ApiOperation("删除模板")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@ApiParam("模板ID") @PathVariable Long id) {
        AiReportTemplate template = new AiReportTemplate();
        template.setId(id);
        template.setDeleted(1);
        templateService.updateById(template);
        return Result.ok();
    }

    private AiReportTemplateDTO convertToDTO(AiReportTemplate entity) {
        AiReportTemplateDTO dto = new AiReportTemplateDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }
}
