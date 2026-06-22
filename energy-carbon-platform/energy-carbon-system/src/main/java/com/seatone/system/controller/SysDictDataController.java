package com.seatone.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.seatone.common.result.PageResult;
import com.seatone.common.result.Result;
import com.seatone.system.entity.SysDictData;
import com.seatone.system.service.SysDictDataService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "字典数据管理")
@RestController
@RequestMapping("/system/dictData")
public class SysDictDataController {

    @Autowired
    private SysDictDataService dictDataService;

    @ApiOperation("分页查询字典数据")
    @GetMapping("/list")
    public Result<PageResult<SysDictData>> list(
            @ApiParam("页码") @RequestParam(defaultValue = "1") Long current,
            @ApiParam("每页条数") @RequestParam(defaultValue = "10") Long size,
            @ApiParam("字典类型") @RequestParam(required = false) String dictType) {
        Page<SysDictData> page = new Page<>(current, size);
        LambdaQueryWrapper<SysDictData> wrapper = new LambdaQueryWrapper<>();
        if (dictType != null && !dictType.isEmpty()) {
            wrapper.eq(SysDictData::getDictType, dictType);
        }
        wrapper.orderByAsc(SysDictData::getSort);
        Page<SysDictData> resultPage = dictDataService.page(page, wrapper);
        return Result.ok(PageResult.of(resultPage.getCurrent(), resultPage.getSize(), resultPage.getTotal(), resultPage.getRecords()));
    }

    @ApiOperation("根据字典类型查询数据")
    @GetMapping("/type/{dictType}")
    public Result<List<SysDictData>> getByType(@ApiParam("字典类型") @PathVariable String dictType) {
        List<SysDictData> list = dictDataService.listByDictType(dictType);
        return Result.ok(list);
    }

    @ApiOperation("根据ID查询字典数据")
    @GetMapping("/{id}")
    public Result<SysDictData> getById(@ApiParam("字典数据ID") @PathVariable Long id) {
        SysDictData dictData = dictDataService.getById(id);
        if (dictData == null) {
            return Result.error("字典数据不存在");
        }
        return Result.ok(dictData);
    }

    @ApiOperation("新增字典数据")
    @PostMapping
    public Result<Void> add(@RequestBody SysDictData dictData) {
        dictDataService.save(dictData);
        return Result.ok();
    }

    @ApiOperation("修改字典数据")
    @PutMapping("/{id}")
    public Result<Void> update(@ApiParam("字典数据ID") @PathVariable Long id, @RequestBody SysDictData dictData) {
        dictData.setId(id);
        dictDataService.updateById(dictData);
        return Result.ok();
    }

    @ApiOperation("删除字典数据")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@ApiParam("字典数据ID") @PathVariable Long id) {
        dictDataService.removeById(id);
        return Result.ok();
    }
}
