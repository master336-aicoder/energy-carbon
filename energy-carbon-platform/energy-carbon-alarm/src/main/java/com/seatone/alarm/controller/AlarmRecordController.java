package com.seatone.alarm.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.seatone.common.result.PageResult;
import com.seatone.common.result.Result;
import com.seatone.alarm.dto.AlarmRecordDTO;
import com.seatone.alarm.entity.AlarmRecord;
import com.seatone.alarm.service.AlarmRecordService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Api(tags = "报警记录管理")
@RestController
@RequestMapping("/alarm/record")
public class AlarmRecordController {

    @Autowired
    private AlarmRecordService recordService;

    @ApiOperation("分页查询报警记录")
    @GetMapping("/list")
    public Result<PageResult<AlarmRecordDTO>> list(
            @ApiParam("页码") @RequestParam(defaultValue = "1") Long current,
            @ApiParam("每页条数") @RequestParam(defaultValue = "10") Long size,
            @ApiParam("报警级别") @RequestParam(required = false) String alarmLevel,
            @ApiParam("处理状态") @RequestParam(required = false) String handleStatus,
            @ApiParam("报警类型") @RequestParam(required = false) String alarmType,
            @ApiParam("开始时间") @RequestParam(required = false) String startTime,
            @ApiParam("结束时间") @RequestParam(required = false) String endTime) {
        Page<AlarmRecord> page = new Page<>(current, size);
        LambdaQueryWrapper<AlarmRecord> wrapper = new LambdaQueryWrapper<>();
        if (alarmLevel != null && !alarmLevel.isEmpty()) {
            wrapper.eq(AlarmRecord::getAlarmLevel, alarmLevel);
        }
        if (handleStatus != null && !handleStatus.isEmpty()) {
            wrapper.eq(AlarmRecord::getHandleStatus, handleStatus);
        }
        if (alarmType != null && !alarmType.isEmpty()) {
            wrapper.eq(AlarmRecord::getAlarmType, alarmType);
        }
        if (startTime != null && !startTime.isEmpty()) {
            wrapper.ge(AlarmRecord::getAlarmTime, LocalDateTime.parse(startTime));
        }
        if (endTime != null && !endTime.isEmpty()) {
            wrapper.le(AlarmRecord::getAlarmTime, LocalDateTime.parse(endTime));
        }
        wrapper.orderByDesc(AlarmRecord::getAlarmTime);
        Page<AlarmRecord> resultPage = recordService.page(page, wrapper);
        List<AlarmRecordDTO> records = resultPage.getRecords().stream().map(this::convertToDTO).collect(Collectors.toList());
        return Result.ok(PageResult.of(resultPage.getCurrent(), resultPage.getSize(), resultPage.getTotal(), records));
    }

    @ApiOperation("处理报警记录")
    @PutMapping("/{id}/handle")
    public Result<Void> handle(@ApiParam("记录ID") @PathVariable Long id,
                                @ApiParam("处理状态") @RequestParam String handleStatus,
                                @ApiParam("处理人") @RequestParam(required = false) String handler,
                                @ApiParam("处理备注") @RequestParam(required = false) String handleRemark) {
        AlarmRecord record = new AlarmRecord();
        record.setId(id);
        record.setHandleStatus(handleStatus);
        record.setHandler(handler);
        record.setHandleRemark(handleRemark);
        record.setHandleTime(LocalDateTime.now());
        recordService.updateById(record);
        return Result.ok();
    }

    @ApiOperation("批量处理报警记录")
    @PutMapping("/batch-handle")
    public Result<Void> batchHandle(@RequestBody Map<String, Object> params) {
        @SuppressWarnings("unchecked")
        List<Long> ids = (List<Long>) params.get("ids");
        String handleStatus = (String) params.get("handleStatus");
        String handler = (String) params.get("handler");
        String handleRemark = (String) params.get("handleRemark");
        for (Long id : ids) {
            AlarmRecord record = new AlarmRecord();
            record.setId(id);
            record.setHandleStatus(handleStatus);
            record.setHandler(handler);
            record.setHandleRemark(handleRemark);
            record.setHandleTime(LocalDateTime.now());
            recordService.updateById(record);
        }
        return Result.ok();
    }

    @ApiOperation("获取报警统计")
    @GetMapping("/statistics")
    public Result<Map<String, Object>> statistics() {
        List<AlarmRecord> all = recordService.list();
        long total = all.size();
        long unhandled = all.stream().filter(r -> "UNHANDLED".equals(r.getHandleStatus())).count();
        long handled = all.stream().filter(r -> "HANDLED".equals(r.getHandleStatus())).count();
        long ignored = all.stream().filter(r -> "IGNORED".equals(r.getHandleStatus())).count();
        long critical = all.stream().filter(r -> "CRITICAL".equals(r.getAlarmLevel())).count();
        long warn = all.stream().filter(r -> "WARN".equals(r.getAlarmLevel())).count();
        long info = all.stream().filter(r -> "INFO".equals(r.getAlarmLevel())).count();

        Map<String, Object> result = new HashMap<>();
        result.put("total", total);
        result.put("unhandled", unhandled);
        result.put("handled", handled);
        result.put("ignored", ignored);
        result.put("critical", critical);
        result.put("warn", warn);
        result.put("info", info);
        return Result.ok(result);
    }

    @ApiOperation("获取报警详情")
    @GetMapping("/{id}")
    public Result<AlarmRecordDTO> getById(@ApiParam("记录ID") @PathVariable Long id) {
        AlarmRecord entity = recordService.getById(id);
        if (entity == null) {
            return Result.error("报警记录不存在");
        }
        return Result.ok(convertToDTO(entity));
    }

    private AlarmRecordDTO convertToDTO(AlarmRecord entity) {
        AlarmRecordDTO dto = new AlarmRecordDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }
}