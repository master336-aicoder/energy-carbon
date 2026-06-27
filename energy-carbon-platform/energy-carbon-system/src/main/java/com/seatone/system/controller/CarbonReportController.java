package com.seatone.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.seatone.common.result.PageResult;
import com.seatone.common.result.Result;
import com.seatone.system.entity.CarbonReport;
import com.seatone.system.mapper.CarbonReportMapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Api(tags = "碳排放报告管理")
@RestController
@RequestMapping("/carbon/report")
public class CarbonReportController {

    @Autowired
    private CarbonReportMapper carbonReportMapper;

    @ApiOperation("分页查询列表")
    @GetMapping("/list")
    public Result<PageResult<CarbonReport>> list(
            @ApiParam("页码") @RequestParam(defaultValue = "1") Long current,
            @ApiParam("每页条数") @RequestParam(defaultValue = "10") Long size,
            @ApiParam("关键词") @RequestParam(required = false) String keyword,
            @ApiParam("状态") @RequestParam(required = false) Integer status
    ) {
        Page<CarbonReport> page = new Page<>(current, size);
        LambdaQueryWrapper<CarbonReport> wrapper = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.like(CarbonReport::getReportName, keyword);
        }
        if (status != null) {
            wrapper.eq(CarbonReport::getStatus, status);
        }
        wrapper.orderByDesc(CarbonReport::getCreateTime);
        Page<CarbonReport> resultPage = carbonReportMapper.selectPage(page, wrapper);
        return Result.ok(PageResult.of(resultPage.getCurrent(), resultPage.getSize(), resultPage.getTotal(), resultPage.getRecords()));
    }

    @ApiOperation("详情")
    @GetMapping("/{id}")
    public Result<CarbonReport> detail(@ApiParam("ID") @PathVariable Long id) {
        CarbonReport entity = carbonReportMapper.selectById(id);
        if (entity == null) {
            return Result.error("记录不存在");
        }
        return Result.ok(entity);
    }

    @ApiOperation("新增")
    @PostMapping
    public Result<Void> add(@RequestBody CarbonReport entity) {
        carbonReportMapper.insert(entity);
        return Result.ok();
    }

    @ApiOperation("修改")
    @PutMapping("/{id}")
    public Result<Void> update(@ApiParam("ID") @PathVariable Long id, @RequestBody CarbonReport entity) {
        entity.setId(id);
        carbonReportMapper.updateById(entity);
        return Result.ok();
    }

    @ApiOperation("删除")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@ApiParam("ID") @PathVariable Long id) {
        carbonReportMapper.deleteById(id);
        return Result.ok();
    }
}
