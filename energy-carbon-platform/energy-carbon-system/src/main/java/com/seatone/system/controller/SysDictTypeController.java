package com.seatone.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.seatone.common.result.PageResult;
import com.seatone.common.result.Result;
import com.seatone.system.entity.SysDictType;
import com.seatone.system.service.SysDictTypeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Api(tags = "字典类型管理")
@RestController
@RequestMapping("/system/dictType")
public class SysDictTypeController {

    @Autowired
    private SysDictTypeService dictTypeService;

    @ApiOperation("分页查询字典类型")
    @GetMapping("/list")
    public Result<PageResult<SysDictType>> list(
            @ApiParam("页码") @RequestParam(defaultValue = "1") Long current,
            @ApiParam("每页条数") @RequestParam(defaultValue = "10") Long size,
            @ApiParam("字典名称") @RequestParam(required = false) String dictName) {
        Page<SysDictType> page = new Page<>(current, size);
        LambdaQueryWrapper<SysDictType> wrapper = new LambdaQueryWrapper<>();
        if (dictName != null && !dictName.isEmpty()) {
            wrapper.like(SysDictType::getDictName, dictName);
        }
        wrapper.orderByDesc(SysDictType::getCreateTime);
        Page<SysDictType> resultPage = dictTypeService.page(page, wrapper);
        return Result.ok(PageResult.of(resultPage.getCurrent(), resultPage.getSize(), resultPage.getTotal(), resultPage.getRecords()));
    }

    @ApiOperation("根据ID查询字典类型")
    @GetMapping("/{id}")
    public Result<SysDictType> getById(@ApiParam("字典类型ID") @PathVariable Long id) {
        SysDictType dictType = dictTypeService.getById(id);
        if (dictType == null) {
            return Result.error("字典类型不存在");
        }
        return Result.ok(dictType);
    }

    @ApiOperation("新增字典类型")
    @PostMapping
    public Result<Void> add(@RequestBody SysDictType dictType) {
        dictTypeService.save(dictType);
        return Result.ok();
    }

    @ApiOperation("修改字典类型")
    @PutMapping("/{id}")
    public Result<Void> update(@ApiParam("字典类型ID") @PathVariable Long id, @RequestBody SysDictType dictType) {
        dictType.setId(id);
        dictTypeService.updateById(dictType);
        return Result.ok();
    }

    @ApiOperation("删除字典类型")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@ApiParam("字典类型ID") @PathVariable Long id) {
        dictTypeService.removeById(id);
        return Result.ok();
    }
}
