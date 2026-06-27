package com.seatone.system.controller;

import com.seatone.common.result.PageResult;
import com.seatone.common.result.Result;
import com.seatone.system.entity.SysLog;
import com.seatone.system.service.SysLogService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

/**
 * 操作日志控制器
 *
 * @author seatone
 * @since 2026-06-27
 */
@Api(tags = "操作日志管理")
@RestController
@RequestMapping("/system/log")
public class SysLogController {

    @Autowired
    private SysLogService sysLogService;

    @ApiOperation("分页查询操作日志")
    @GetMapping("/list")
    public Result<PageResult<SysLog>> list(
            @ApiParam("页码") @RequestParam(defaultValue = "1") Long current,
            @ApiParam("每页条数") @RequestParam(defaultValue = "10") Long size,
            @ApiParam("操作人") @RequestParam(required = false) String operName,
            @ApiParam("操作模块") @RequestParam(required = false) String title,
            @ApiParam("业务类型") @RequestParam(required = false) Integer businessType,
            @ApiParam("操作状态") @RequestParam(required = false) Integer status,
            @ApiParam("开始时间") @RequestParam(required = false) String startTime,
            @ApiParam("结束时间") @RequestParam(required = false) String endTime) {

        Page<SysLog> resultPage = sysLogService.listLogs(current, size, operName, title,
                businessType, status, startTime, endTime);

        return Result.ok(PageResult.of(resultPage.getCurrent(), resultPage.getSize(),
                resultPage.getTotal(), resultPage.getRecords()));
    }
}
