package com.seatone.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.seatone.common.result.PageResult;
import com.seatone.common.result.Result;
import com.seatone.system.entity.EfficiencyBenchmark;
import com.seatone.system.mapper.EfficiencyBenchmarkMapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Api(tags = "能效对标管理")
@RestController
@RequestMapping("/efficiency/benchmark")
public class EfficiencyBenchmarkController {

    @Autowired
    private EfficiencyBenchmarkMapper efficiencyBenchmarkMapper;

    @ApiOperation("分页查询列表")
    @GetMapping("/list")
    public Result<PageResult<EfficiencyBenchmark>> list(
            @ApiParam("页码") @RequestParam(defaultValue = "1") Long current,
            @ApiParam("每页条数") @RequestParam(defaultValue = "10") Long size,
            @ApiParam("关键词") @RequestParam(required = false) String keyword,
            @ApiParam("状态") @RequestParam(required = false) Integer status
    ) {
        Page<EfficiencyBenchmark> page = new Page<>(current, size);
        LambdaQueryWrapper<EfficiencyBenchmark> wrapper = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.like(EfficiencyBenchmark::getBenchmarkName, keyword);
        }
        if (status != null) {
            wrapper.eq(EfficiencyBenchmark::getStatus, status);
        }
        wrapper.orderByDesc(EfficiencyBenchmark::getCreateTime);
        Page<EfficiencyBenchmark> resultPage = efficiencyBenchmarkMapper.selectPage(page, wrapper);
        return Result.ok(PageResult.of(resultPage.getCurrent(), resultPage.getSize(), resultPage.getTotal(), resultPage.getRecords()));
    }

    @ApiOperation("详情")
    @GetMapping("/{id}")
    public Result<EfficiencyBenchmark> detail(@ApiParam("ID") @PathVariable Long id) {
        EfficiencyBenchmark entity = efficiencyBenchmarkMapper.selectById(id);
        if (entity == null) {
            return Result.error("记录不存在");
        }
        return Result.ok(entity);
    }

    @ApiOperation("新增")
    @PostMapping
    public Result<Void> add(@RequestBody EfficiencyBenchmark entity) {
        efficiencyBenchmarkMapper.insert(entity);
        return Result.ok();
    }

    @ApiOperation("修改")
    @PutMapping("/{id}")
    public Result<Void> update(@ApiParam("ID") @PathVariable Long id, @RequestBody EfficiencyBenchmark entity) {
        entity.setId(id);
        efficiencyBenchmarkMapper.updateById(entity);
        return Result.ok();
    }

    @ApiOperation("删除")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@ApiParam("ID") @PathVariable Long id) {
        efficiencyBenchmarkMapper.deleteById(id);
        return Result.ok();
    }
}
