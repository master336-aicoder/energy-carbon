package com.seatone.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.seatone.common.result.PageResult;
import com.seatone.common.result.Result;
import com.seatone.system.entity.ReportIndicator;
import com.seatone.system.mapper.ReportIndicatorMapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Api(tags = "报表指标管理")
@RestController
@RequestMapping("/report/indicator")
public class ReportIndicatorController {

    @Autowired
    private ReportIndicatorMapper reportIndicatorMapper;

    @ApiOperation("分页查询列表")
    @GetMapping("/list")
    public Result<PageResult<ReportIndicator>> list(
            @ApiParam("页码") @RequestParam(defaultValue = "1") Long current,
            @ApiParam("每页条数") @RequestParam(defaultValue = "10") Long size,
            @ApiParam("关键词") @RequestParam(required = false) String keyword,
            @ApiParam("状态") @RequestParam(required = false) Integer status
    ) {
        Page<ReportIndicator> page = new Page<>(current, size);
        LambdaQueryWrapper<ReportIndicator> wrapper = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.like(ReportIndicator::getIndicatorName, keyword);
        }
        if (status != null) {
            wrapper.eq(ReportIndicator::getStatus, status);
        }
        wrapper.orderByDesc(ReportIndicator::getCreateTime);
        Page<ReportIndicator> resultPage = reportIndicatorMapper.selectPage(page, wrapper);
        return Result.ok(PageResult.of(resultPage.getCurrent(), resultPage.getSize(), resultPage.getTotal(), resultPage.getRecords()));
    }

    @ApiOperation("详情")
    @GetMapping("/{id}")
    public Result<ReportIndicator> detail(@ApiParam("ID") @PathVariable Long id) {
        ReportIndicator entity = reportIndicatorMapper.selectById(id);
        if (entity == null) {
            return Result.error("记录不存在");
        }
        return Result.ok(entity);
    }

    @ApiOperation("新增")
    @PostMapping
    public Result<Void> add(@RequestBody ReportIndicator entity) {
        reportIndicatorMapper.insert(entity);
        return Result.ok();
    }

    @ApiOperation("修改")
    @PutMapping("/{id}")
    public Result<Void> update(@ApiParam("ID") @PathVariable Long id, @RequestBody ReportIndicator entity) {
        entity.setId(id);
        reportIndicatorMapper.updateById(entity);
        return Result.ok();
    }

    @ApiOperation("删除")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@ApiParam("ID") @PathVariable Long id) {
        reportIndicatorMapper.deleteById(id);
        return Result.ok();
    }
}
