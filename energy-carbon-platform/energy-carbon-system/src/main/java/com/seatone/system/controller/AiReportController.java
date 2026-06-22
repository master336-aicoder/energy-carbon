package com.seatone.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.seatone.common.result.PageResult;
import com.seatone.common.result.Result;
import com.seatone.system.dto.AiReportTaskDTO;
import com.seatone.system.entity.AiReportTask;
import com.seatone.system.service.AiReportTaskService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Api(tags = "AI报告生成管理")
@RestController
@RequestMapping("/ai/report")
public class AiReportController {

    @Autowired
    private AiReportTaskService reportTaskService;

    @ApiOperation("分页查询报告任务列表")
    @GetMapping("/list")
    public Result<PageResult<AiReportTaskDTO>> list(
            @ApiParam("页码") @RequestParam(defaultValue = "1") Long current,
            @ApiParam("每页条数") @RequestParam(defaultValue = "10") Long size,
            @ApiParam("报告类型") @RequestParam(required = false) String reportType,
            @ApiParam("状态") @RequestParam(required = false) String status) {
        Page<AiReportTask> page = new Page<>(current, size);
        LambdaQueryWrapper<AiReportTask> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AiReportTask::getDeleted, 0);
        if (reportType != null && !reportType.isEmpty()) {
            wrapper.eq(AiReportTask::getReportType, reportType);
        }
        if (status != null && !status.isEmpty()) {
            wrapper.eq(AiReportTask::getStatus, status);
        }
        wrapper.orderByDesc(AiReportTask::getCreateTime);
        Page<AiReportTask> resultPage = reportTaskService.page(page, wrapper);
        List<AiReportTaskDTO> records = resultPage.getRecords().stream().map(this::convertToDTO).collect(Collectors.toList());
        return Result.ok(PageResult.of(resultPage.getCurrent(), resultPage.getSize(), resultPage.getTotal(), records));
    }

    @ApiOperation("创建报告任务")
    @PostMapping
    public Result<AiReportTaskDTO> create(@RequestBody AiReportTask task) {
        task.setStatus("PENDING");
        task.setProgress(0);
        task.setDeleted(0);
        reportTaskService.save(task);
        return Result.ok(convertToDTO(task));
    }

    @ApiOperation("查询任务状态")
    @GetMapping("/{id}")
    public Result<AiReportTaskDTO> getById(@ApiParam("任务ID") @PathVariable Long id) {
        AiReportTask entity = reportTaskService.getById(id);
        if (entity == null) {
            return Result.error("报告任务不存在");
        }
        return Result.ok(convertToDTO(entity));
    }

    @ApiOperation("取消任务")
    @PutMapping("/{id}/cancel")
    public Result<Void> cancel(@ApiParam("任务ID") @PathVariable Long id) {
        AiReportTask task = new AiReportTask();
        task.setId(id);
        task.setStatus("FAILED");
        task.setErrorMsg("用户取消任务");
        reportTaskService.updateById(task);
        return Result.ok();
    }

    @ApiOperation("删除报告任务")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@ApiParam("任务ID") @PathVariable Long id) {
        AiReportTask task = new AiReportTask();
        task.setId(id);
        task.setDeleted(1);
        reportTaskService.updateById(task);
        return Result.ok();
    }

    private AiReportTaskDTO convertToDTO(AiReportTask entity) {
        AiReportTaskDTO dto = new AiReportTaskDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }
}
