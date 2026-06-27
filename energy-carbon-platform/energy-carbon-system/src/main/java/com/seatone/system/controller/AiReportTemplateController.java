package com.seatone.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.seatone.common.result.PageResult;
import com.seatone.common.result.Result;
import com.seatone.system.entity.AiReportTemplate;
import com.seatone.system.service.AiReportTemplateService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "报告模板管理")
@RestController
@RequestMapping("/ai/report-template")
public class AiReportTemplateController {

    @Autowired
    private AiReportTemplateService aiReportTemplateService;

    @ApiOperation("查询所有报告模板（不分页）")
    @GetMapping("/all")
    public Result<List<AiReportTemplate>> all() {
        List<AiReportTemplate> list = aiReportTemplateService.list();
        return Result.ok(list);
    }

    @ApiOperation("分页查询列表")
    @GetMapping("/list")
    public Result<PageResult<AiReportTemplate>> list(
            @ApiParam("页码") @RequestParam(defaultValue = "1") Long current,
            @ApiParam("每页条数") @RequestParam(defaultValue = "10") Long size,
            @ApiParam("关键词") @RequestParam(required = false) String keyword,
            @ApiParam("状态") @RequestParam(required = false) Integer status
    ) {
        Page<AiReportTemplate> page = new Page<>(current, size);
        LambdaQueryWrapper<AiReportTemplate> wrapper = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.like(AiReportTemplate::getTemplateName, keyword);
        }
        if (status != null) {
            wrapper.eq(AiReportTemplate::getStatus, status);
        }
        wrapper.orderByDesc(AiReportTemplate::getCreateTime);
        Page<AiReportTemplate> resultPage = aiReportTemplateService.page(page, wrapper);
        return Result.ok(PageResult.of(resultPage.getCurrent(), resultPage.getSize(), resultPage.getTotal(), resultPage.getRecords()));
    }

    @ApiOperation("详情")
    @GetMapping("/{id}")
    public Result<AiReportTemplate> detail(@ApiParam("ID") @PathVariable Long id) {
        AiReportTemplate entity = aiReportTemplateService.getById(id);
        if (entity == null) {
            return Result.error("记录不存在");
        }
        return Result.ok(entity);
    }

    @ApiOperation("新增")
    @PostMapping
    public Result<Void> add(@RequestBody AiReportTemplate entity) {
        aiReportTemplateService.save(entity);
        return Result.ok();
    }

    @ApiOperation("修改")
    @PutMapping("/{id}")
    public Result<Void> update(@ApiParam("ID") @PathVariable Long id, @RequestBody AiReportTemplate entity) {
        entity.setId(id);
        aiReportTemplateService.updateById(entity);
        return Result.ok();
    }

    @ApiOperation("删除")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@ApiParam("ID") @PathVariable Long id) {
        aiReportTemplateService.removeById(id);
        return Result.ok();
    }
}
