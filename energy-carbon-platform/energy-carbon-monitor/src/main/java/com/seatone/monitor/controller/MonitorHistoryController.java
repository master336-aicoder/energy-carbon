package com.seatone.monitor.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.seatone.common.result.PageResult;
import com.seatone.common.result.Result;
import com.seatone.monitor.dto.MonitorHistoryDataDTO;
import com.seatone.monitor.entity.MonitorHistoryData;
import com.seatone.monitor.service.MonitorHistoryDataService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Api(tags = "历史数据查询")
@RestController
@RequestMapping("/monitor/history")
public class MonitorHistoryController {

    @Autowired
    private MonitorHistoryDataService historyDataService;

    @ApiOperation("分页查询历史数据")
    @GetMapping("/list")
    public Result<PageResult<MonitorHistoryDataDTO>> list(
            @ApiParam("页码") @RequestParam(defaultValue = "1") Long current,
            @ApiParam("每页条数") @RequestParam(defaultValue = "10") Long size,
            @ApiParam("计量器具ID") @RequestParam(required = false) Long meterId,
            @ApiParam("数据类型") @RequestParam(required = false) String dataType,
            @ApiParam("开始时间") @RequestParam(required = false) String startTime,
            @ApiParam("结束时间") @RequestParam(required = false) String endTime) {
        Page<MonitorHistoryData> page = new Page<>(current, size);
        LambdaQueryWrapper<MonitorHistoryData> wrapper = new LambdaQueryWrapper<>();
        if (meterId != null) {
            wrapper.eq(MonitorHistoryData::getMeterId, meterId);
        }
        if (dataType != null && !dataType.isEmpty()) {
            wrapper.eq(MonitorHistoryData::getDataType, dataType);
        }
        if (startTime != null && !startTime.isEmpty()) {
            wrapper.ge(MonitorHistoryData::getDataTime, LocalDateTime.parse(startTime));
        }
        if (endTime != null && !endTime.isEmpty()) {
            wrapper.le(MonitorHistoryData::getDataTime, LocalDateTime.parse(endTime));
        }
        wrapper.orderByDesc(MonitorHistoryData::getDataTime);
        Page<MonitorHistoryData> resultPage = historyDataService.page(page, wrapper);
        List<MonitorHistoryDataDTO> records = resultPage.getRecords().stream().map(this::convertToDTO).collect(Collectors.toList());
        return Result.ok(PageResult.of(resultPage.getCurrent(), resultPage.getSize(), resultPage.getTotal(), records));
    }

    @ApiOperation("查询历史数据曲线（不翻页）")
    @GetMapping("/chart")
    public Result<List<MonitorHistoryDataDTO>> chartData(
            @ApiParam("计量器具ID") @RequestParam Long meterId,
            @ApiParam("数据类型") @RequestParam(required = false) String dataType,
            @ApiParam("开始时间") @RequestParam String startTime,
            @ApiParam("结束时间") @RequestParam String endTime) {
        LambdaQueryWrapper<MonitorHistoryData> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MonitorHistoryData::getMeterId, meterId);
        if (dataType != null && !dataType.isEmpty()) {
            wrapper.eq(MonitorHistoryData::getDataType, dataType);
        }
        wrapper.ge(MonitorHistoryData::getDataTime, LocalDateTime.parse(startTime));
        wrapper.le(MonitorHistoryData::getDataTime, LocalDateTime.parse(endTime));
        wrapper.orderByAsc(MonitorHistoryData::getDataTime);
        List<MonitorHistoryData> list = historyDataService.list(wrapper);
        List<MonitorHistoryDataDTO> records = list.stream().map(this::convertToDTO).collect(Collectors.toList());
        return Result.ok(records);
    }

    @ApiOperation("获取数据类型列表")
    @GetMapping("/data-types")
    public Result<List<String>> getDataTypes() {
        List<MonitorHistoryData> list = historyDataService.list(
                new LambdaQueryWrapper<MonitorHistoryData>()
                        .select(MonitorHistoryData::getDataType)
                        .groupBy(MonitorHistoryData::getDataType));
        List<String> dataTypes = list.stream().map(MonitorHistoryData::getDataType).collect(Collectors.toList());
        return Result.ok(dataTypes);
    }

    private MonitorHistoryDataDTO convertToDTO(MonitorHistoryData entity) {
        MonitorHistoryDataDTO dto = new MonitorHistoryDataDTO();
        dto.setId(entity.getId());
        dto.setMeterId(entity.getMeterId());
        dto.setDataTime(entity.getDataTime());
        dto.setDataValue(entity.getDataValue());
        dto.setDataType(entity.getDataType());
        dto.setUnit(entity.getUnit());
        dto.setCreateTime(entity.getCreateTime());
        return dto;
    }
}