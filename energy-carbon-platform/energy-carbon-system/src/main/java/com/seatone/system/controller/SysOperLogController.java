package com.seatone.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.seatone.common.result.PageResult;
import com.seatone.common.result.Result;
import com.seatone.system.entity.SysOperLog;
import com.seatone.system.service.SysOperLogService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Api(tags = "操作日志管理")
@RestController
@RequestMapping("/system/operLog")
public class SysOperLogController {

    @Autowired
    private SysOperLogService operLogService;

    @ApiOperation("分页查询操作日志")
    @GetMapping("/list")
    public Result<PageResult<SysOperLog>> list(
            @ApiParam("页码") @RequestParam(defaultValue = "1") Long current,
            @ApiParam("每页条数") @RequestParam(defaultValue = "10") Long size,
            @ApiParam("操作人") @RequestParam(required = false) String operName,
            @ApiParam("状态") @RequestParam(required = false) Integer status) {
        Page<SysOperLog> page = new Page<>(current, size);
        LambdaQueryWrapper<SysOperLog> wrapper = new LambdaQueryWrapper<>();
        if (operName != null && !operName.isEmpty()) {
            wrapper.like(SysOperLog::getOperName, operName);
        }
        if (status != null) {
            wrapper.eq(SysOperLog::getStatus, status);
        }
        wrapper.orderByDesc(SysOperLog::getOperTime);
        Page<SysOperLog> resultPage = operLogService.page(page, wrapper);
        return Result.ok(PageResult.of(resultPage.getCurrent(), resultPage.getSize(), resultPage.getTotal(), resultPage.getRecords()));
    }

    @ApiOperation("清空操作日志")
    @DeleteMapping("/clear")
    public Result<Void> clear() {
        operLogService.clearLog();
        return Result.ok();
    }
}
