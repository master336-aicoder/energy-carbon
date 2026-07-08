package com.seatone.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.seatone.common.result.PageResult;
import com.seatone.common.result.Result;
import com.seatone.system.entity.AiServiceRecord;
import com.seatone.system.service.AiServiceRecordService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Api(tags = "AI服务预约管理")
@RestController
@RequestMapping("/ai/service-record")
public class AiServiceRecordController {

    @Autowired
    private AiServiceRecordService aiServiceRecordService;

    @ApiOperation("新增服务预约记录")
    @PostMapping
    public Result<Void> add(@RequestBody @Valid AiServiceRecord entity) {
        if (entity.getStatus() == null) {
            entity.setStatus(1);
        }
        aiServiceRecordService.save(entity);
        return Result.ok();
    }

    @ApiOperation("修改服务预约记录")
    @PutMapping("/{id}")
    public Result<Void> update(@ApiParam("ID") @PathVariable Long id, @RequestBody @Valid AiServiceRecord entity) {
        entity.setId(id);
        aiServiceRecordService.updateById(entity);
        return Result.ok();
    }

    @ApiOperation("取消预约")
    @PutMapping("/{id}/cancel")
    public Result<Void> cancel(@ApiParam("ID") @PathVariable Long id) {
        AiServiceRecord entity = aiServiceRecordService.getById(id);
        if (entity == null) {
            return Result.error("记录不存在");
        }
        entity.setStatus(0);
        aiServiceRecordService.updateById(entity);
        return Result.ok();
    }

    @ApiOperation("删除预约记录")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@ApiParam("ID") @PathVariable Long id) {
        aiServiceRecordService.removeById(id);
        return Result.ok();
    }

    @ApiOperation("查询预约列表（分页）")
    @GetMapping("/list")
    public Result<PageResult<AiServiceRecord>> list(
            @ApiParam("页码") @RequestParam(defaultValue = "1") Long current,
            @ApiParam("每页条数") @RequestParam(defaultValue = "10") Long size,
            @ApiParam("服务名称") @RequestParam(required = false) String serviceName,
            @ApiParam("状态") @RequestParam(required = false) String status
    ) {
        Page<AiServiceRecord> page = new Page<>(current, size);
        LambdaQueryWrapper<AiServiceRecord> wrapper = new LambdaQueryWrapper<>();
        if (serviceName != null && !serviceName.isEmpty()) {
            wrapper.like(AiServiceRecord::getServiceName, serviceName);
        }
        if (status != null && !status.isEmpty()) {
            wrapper.eq(AiServiceRecord::getStatus, status);
        }
        wrapper.orderByDesc(AiServiceRecord::getCreateTime);
        Page<AiServiceRecord> resultPage = aiServiceRecordService.page(page, wrapper);
        return Result.ok(PageResult.of(resultPage.getCurrent(), resultPage.getSize(), resultPage.getTotal(), resultPage.getRecords()));
    }
}
