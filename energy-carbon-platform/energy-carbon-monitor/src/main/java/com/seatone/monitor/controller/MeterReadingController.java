package com.seatone.monitor.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.seatone.common.result.PageResult;
import com.seatone.common.result.Result;
import com.seatone.monitor.dto.MeterRecordDTO;
import com.seatone.monitor.entity.MonitorMeterRecord;
import com.seatone.monitor.service.MonitorMeterRecordService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Api(tags = "计量抄表")
@RestController
@RequestMapping("/monitor/meter-record")
public class MeterReadingController {

    @Autowired
    private MonitorMeterRecordService recordService;

    @ApiOperation("分页查询抄表记录")
    @GetMapping("/list")
    public Result<PageResult<MeterRecordDTO>> list(
            @ApiParam("页码") @RequestParam(defaultValue = "1") Long current,
            @ApiParam("每页条数") @RequestParam(defaultValue = "10") Long size,
            @ApiParam("计量器具ID") @RequestParam(required = false) Long meterId,
            @ApiParam("开始时间") @RequestParam(required = false) String startTime,
            @ApiParam("结束时间") @RequestParam(required = false) String endTime) {
        Page<MonitorMeterRecord> page = new Page<>(current, size);
        LambdaQueryWrapper<MonitorMeterRecord> wrapper = new LambdaQueryWrapper<>();
        if (meterId != null) {
            wrapper.eq(MonitorMeterRecord::getMeterId, meterId);
        }
        if (startTime != null && !startTime.isEmpty()) {
            wrapper.ge(MonitorMeterRecord::getRecordTime, LocalDateTime.parse(startTime));
        }
        if (endTime != null && !endTime.isEmpty()) {
            wrapper.le(MonitorMeterRecord::getRecordTime, LocalDateTime.parse(endTime));
        }
        wrapper.orderByDesc(MonitorMeterRecord::getRecordTime);
        Page<MonitorMeterRecord> resultPage = recordService.page(page, wrapper);
        List<MeterRecordDTO> records = resultPage.getRecords().stream().map(this::convertToDTO).collect(Collectors.toList());
        return Result.ok(PageResult.of(resultPage.getCurrent(), resultPage.getSize(), resultPage.getTotal(), records));
    }

    @ApiOperation("获取上次抄表记录（用于自动带入初始表码）")
    @GetMapping("/last/{meterId}")
    public Result<MeterRecordDTO> getLastRecord(@ApiParam("计量器具ID") @PathVariable Long meterId) {
        LambdaQueryWrapper<MonitorMeterRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MonitorMeterRecord::getMeterId, meterId);
        wrapper.orderByDesc(MonitorMeterRecord::getRecordTime);
        wrapper.last("LIMIT 1");
        MonitorMeterRecord record = recordService.getOne(wrapper);
        if (record == null) {
            return Result.ok(new MeterRecordDTO());
        }
        return Result.ok(convertToDTO(record));
    }

    @ApiOperation("新增抄表记录（自动计算用量和费用）")
    @PostMapping
    public Result<Void> add(@RequestBody MeterRecordDTO dto) {
        MonitorMeterRecord record = new MonitorMeterRecord();
        BeanUtils.copyProperties(dto, record);
        record.setId(null);
        // 自动计算用量
        if (record.getStartCode() != null && record.getEndCode() != null) {
            record.setConsumption(record.getEndCode().subtract(record.getStartCode()));
        }
        // 自动计算费用
        if (record.getConsumption() != null && record.getUnitPrice() != null) {
            record.setCost(record.getConsumption().multiply(record.getUnitPrice()));
        }
        recordService.save(record);
        return Result.ok();
    }

    @ApiOperation("批量新增抄表记录")
    @PostMapping("/batch")
    public Result<Void> batchAdd(@RequestBody List<MeterRecordDTO> dtoList) {
        for (MeterRecordDTO dto : dtoList) {
            MonitorMeterRecord record = new MonitorMeterRecord();
            BeanUtils.copyProperties(dto, record);
            record.setId(null);
            if (record.getStartCode() != null && record.getEndCode() != null) {
                record.setConsumption(record.getEndCode().subtract(record.getStartCode()));
            }
            if (record.getConsumption() != null && record.getUnitPrice() != null) {
                record.setCost(record.getConsumption().multiply(record.getUnitPrice()));
            }
            recordService.save(record);
        }
        return Result.ok();
    }

    @ApiOperation("修改抄表记录")
    @PutMapping("/{id}")
    public Result<Void> update(@ApiParam("记录ID") @PathVariable Long id, @RequestBody MeterRecordDTO dto) {
        MonitorMeterRecord record = new MonitorMeterRecord();
        BeanUtils.copyProperties(dto, record);
        record.setId(id);
        if (record.getStartCode() != null && record.getEndCode() != null) {
            record.setConsumption(record.getEndCode().subtract(record.getStartCode()));
        }
        if (record.getConsumption() != null && record.getUnitPrice() != null) {
            record.setCost(record.getConsumption().multiply(record.getUnitPrice()));
        }
        recordService.updateById(record);
        return Result.ok();
    }

    @ApiOperation("删除抄表记录")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@ApiParam("记录ID") @PathVariable Long id) {
        recordService.removeById(id);
        return Result.ok();
    }

    private MeterRecordDTO convertToDTO(MonitorMeterRecord record) {
        MeterRecordDTO dto = new MeterRecordDTO();
        BeanUtils.copyProperties(record, dto);
        return dto;
    }
}