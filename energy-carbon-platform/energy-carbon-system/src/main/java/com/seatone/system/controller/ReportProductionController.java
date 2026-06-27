package com.seatone.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.seatone.common.result.PageResult;
import com.seatone.common.result.Result;
import com.seatone.system.entity.ReportProduction;
import com.seatone.system.mapper.ReportProductionMapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Api(tags = "产量数据管理")
@RestController
@RequestMapping("/report/production")
public class ReportProductionController {

    @Autowired
    private ReportProductionMapper reportProductionMapper;

    @ApiOperation("分页查询列表")
    @GetMapping("/list")
    public Result<PageResult<ReportProduction>> list(
            @ApiParam("页码") @RequestParam(defaultValue = "1") Long current,
            @ApiParam("每页条数") @RequestParam(defaultValue = "10") Long size,
            @ApiParam("关键词") @RequestParam(required = false) String keyword,
            @ApiParam("状态") @RequestParam(required = false) Integer status
    ) {
        Page<ReportProduction> page = new Page<>(current, size);
        LambdaQueryWrapper<ReportProduction> wrapper = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.like(ReportProduction::getProductName, keyword);
        }
        if (status != null) {
            wrapper.eq(ReportProduction::getStatus, status);
        }
        wrapper.orderByDesc(ReportProduction::getCreateTime);
        Page<ReportProduction> resultPage = reportProductionMapper.selectPage(page, wrapper);
        return Result.ok(PageResult.of(resultPage.getCurrent(), resultPage.getSize(), resultPage.getTotal(), resultPage.getRecords()));
    }

    @ApiOperation("详情")
    @GetMapping("/{id}")
    public Result<ReportProduction> detail(@ApiParam("ID") @PathVariable Long id) {
        ReportProduction entity = reportProductionMapper.selectById(id);
        if (entity == null) {
            return Result.error("记录不存在");
        }
        return Result.ok(entity);
    }

    @ApiOperation("新增")
    @PostMapping
    public Result<Void> add(@RequestBody ReportProduction entity) {
        reportProductionMapper.insert(entity);
        return Result.ok();
    }

    @ApiOperation("修改")
    @PutMapping("/{id}")
    public Result<Void> update(@ApiParam("ID") @PathVariable Long id, @RequestBody ReportProduction entity) {
        entity.setId(id);
        reportProductionMapper.updateById(entity);
        return Result.ok();
    }

    @ApiOperation("删除")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@ApiParam("ID") @PathVariable Long id) {
        reportProductionMapper.deleteById(id);
        return Result.ok();
    }
}
