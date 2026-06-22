package com.seatone.monitor.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.seatone.common.result.PageResult;
import com.seatone.common.result.Result;
import com.seatone.monitor.dto.StatusLogDTO;
import com.seatone.monitor.entity.MonitorStatusLog;
import com.seatone.monitor.service.MonitorStatusLogService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Api(tags = "状态监测")
@RestController
@RequestMapping("/monitor/status")
public class StatusMonitorController {

    @Autowired
    private MonitorStatusLogService statusLogService;

    @ApiOperation("分页查询状态日志")
    @GetMapping("/list")
    public Result<PageResult<StatusLogDTO>> list(
            @ApiParam("页码") @RequestParam(defaultValue = "1") Long current,
            @ApiParam("每页条数") @RequestParam(defaultValue = "10") Long size,
            @ApiParam("设备ID") @RequestParam(required = false) Long deviceId,
            @ApiParam("设备类型") @RequestParam(required = false) String deviceType,
            @ApiParam("在线状态") @RequestParam(required = false) Integer onlineStatus) {
        Page<MonitorStatusLog> page = new Page<>(current, size);
        LambdaQueryWrapper<MonitorStatusLog> wrapper = new LambdaQueryWrapper<>();
        if (deviceId != null) {
            wrapper.eq(MonitorStatusLog::getDeviceId, deviceId);
        }
        if (deviceType != null && !deviceType.isEmpty()) {
            wrapper.eq(MonitorStatusLog::getDeviceType, deviceType);
        }
        if (onlineStatus != null) {
            wrapper.eq(MonitorStatusLog::getOnlineStatus, onlineStatus);
        }
        wrapper.orderByDesc(MonitorStatusLog::getCreateTime);
        Page<MonitorStatusLog> resultPage = statusLogService.page(page, wrapper);
        List<StatusLogDTO> records = resultPage.getRecords().stream().map(this::convertToDTO).collect(Collectors.toList());
        return Result.ok(PageResult.of(resultPage.getCurrent(), resultPage.getSize(), resultPage.getTotal(), records));
    }

    @ApiOperation("获取设备最新状态")
    @GetMapping("/latest")
    public Result<List<StatusLogDTO>> getLatestStatus(
            @ApiParam("设备类型") @RequestParam(required = false) String deviceType) {
        LambdaQueryWrapper<MonitorStatusLog> wrapper = new LambdaQueryWrapper<>();
        if (deviceType != null && !deviceType.isEmpty()) {
            wrapper.eq(MonitorStatusLog::getDeviceType, deviceType);
        }
        wrapper.orderByDesc(MonitorStatusLog::getCreateTime);
        wrapper.last("LIMIT 100");
        List<MonitorStatusLog> list = statusLogService.list(wrapper);
        List<StatusLogDTO> records = list.stream().map(this::convertToDTO).collect(Collectors.toList());
        return Result.ok(records);
    }

    @ApiOperation("获取在线率统计")
    @GetMapping("/online-rate")
    public Result<Map<String, Object>> getOnlineRate(
            @ApiParam("设备类型") @RequestParam(required = false) String deviceType) {
        LambdaQueryWrapper<MonitorStatusLog> wrapper = new LambdaQueryWrapper<>();
        if (deviceType != null && !deviceType.isEmpty()) {
            wrapper.eq(MonitorStatusLog::getDeviceType, deviceType);
        }
        List<MonitorStatusLog> allLogs = statusLogService.list(wrapper);
        long total = allLogs.size();
        long online = allLogs.stream().filter(log -> log.getOnlineStatus() == 1).count();
        Map<String, Object> result = new HashMap<>();
        result.put("total", total);
        result.put("online", online);
        result.put("offline", total - online);
        result.put("onlineRate", total > 0 ? (double) online / total * 100 : 0);
        return Result.ok(result);
    }

    private StatusLogDTO convertToDTO(MonitorStatusLog entity) {
        StatusLogDTO dto = new StatusLogDTO();
        dto.setId(entity.getId());
        dto.setDeviceId(entity.getDeviceId());
        dto.setDeviceType(entity.getDeviceType());
        dto.setOnlineStatus(entity.getOnlineStatus());
        dto.setOfflineTime(entity.getOfflineTime());
        dto.setOnlineTime(entity.getOnlineTime());
        dto.setDuration(entity.getDuration());
        dto.setCreateTime(entity.getCreateTime());
        return dto;
    }
}