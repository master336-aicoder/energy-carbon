package com.seatone.alarm.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.seatone.common.result.PageResult;
import com.seatone.common.result.Result;
import com.seatone.alarm.dto.AlarmThresholdDTO;
import com.seatone.alarm.entity.AlarmThreshold;
import com.seatone.alarm.service.AlarmThresholdService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Api(tags = "报警阈值管理")
@RestController
@RequestMapping("/alarm/threshold")
public class AlarmThresholdController {

    @Autowired
    private AlarmThresholdService thresholdService;

    @ApiOperation("分页查询阈值列表")
    @GetMapping("/list")
    public Result<PageResult<AlarmThresholdDTO>> list(
            @ApiParam("页码") @RequestParam(defaultValue = "1") Long current,
            @ApiParam("每页条数") @RequestParam(defaultValue = "10") Long size,
            @ApiParam("阈值名称") @RequestParam(required = false) String thresholdName,
            @ApiParam("指标类型") @RequestParam(required = false) String metricType,
            @ApiParam("报警级别") @RequestParam(required = false) String alarmLevel,
            @ApiParam("启用状态") @RequestParam(required = false) Integer enabled) {
        Page<AlarmThreshold> page = new Page<>(current, size);
        LambdaQueryWrapper<AlarmThreshold> wrapper = new LambdaQueryWrapper<>();
        if (thresholdName != null && !thresholdName.isEmpty()) {
            wrapper.like(AlarmThreshold::getThresholdName, thresholdName);
        }
        if (metricType != null && !metricType.isEmpty()) {
            wrapper.eq(AlarmThreshold::getMetricType, metricType);
        }
        if (alarmLevel != null && !alarmLevel.isEmpty()) {
            wrapper.eq(AlarmThreshold::getAlarmLevel, alarmLevel);
        }
        if (enabled != null) {
            wrapper.eq(AlarmThreshold::getEnabled, enabled);
        }
        wrapper.orderByDesc(AlarmThreshold::getCreateTime);
        Page<AlarmThreshold> resultPage = thresholdService.page(page, wrapper);
        List<AlarmThresholdDTO> records = resultPage.getRecords().stream().map(this::convertToDTO).collect(Collectors.toList());
        return Result.ok(PageResult.of(resultPage.getCurrent(), resultPage.getSize(), resultPage.getTotal(), records));
    }

    @ApiOperation("获取所有启用的阈值")
    @GetMapping("/enabled")
    public Result<List<AlarmThresholdDTO>> getEnabledThresholds() {
        List<AlarmThreshold> list = thresholdService.lambdaQuery()
                .eq(AlarmThreshold::getEnabled, 1)
                .list();
        List<AlarmThresholdDTO> records = list.stream().map(this::convertToDTO).collect(Collectors.toList());
        return Result.ok(records);
    }

    @ApiOperation("根据ID查询阈值")
    @GetMapping("/{id}")
    public Result<AlarmThresholdDTO> getById(@ApiParam("阈值ID") @PathVariable Long id) {
        AlarmThreshold entity = thresholdService.getById(id);
        if (entity == null) {
            return Result.error("阈值不存在");
        }
        return Result.ok(convertToDTO(entity));
    }

    @ApiOperation("新增阈值")
    @PostMapping
    public Result<Void> add(@RequestBody AlarmThresholdDTO dto) {
        AlarmThreshold entity = new AlarmThreshold();
        BeanUtils.copyProperties(dto, entity);
        entity.setId(null);
        thresholdService.save(entity);
        return Result.ok();
    }

    @ApiOperation("修改阈值")
    @PutMapping("/{id}")
    public Result<Void> update(@ApiParam("阈值ID") @PathVariable Long id, @RequestBody AlarmThresholdDTO dto) {
        AlarmThreshold entity = new AlarmThreshold();
        BeanUtils.copyProperties(dto, entity);
        entity.setId(id);
        thresholdService.updateById(entity);
        return Result.ok();
    }

    @ApiOperation("启用/禁用阈值")
    @PutMapping("/{id}/enabled")
    public Result<Void> setEnabled(@ApiParam("阈值ID") @PathVariable Long id,
                                    @ApiParam("启用状态") @RequestParam Integer enabled) {
        AlarmThreshold entity = new AlarmThreshold();
        entity.setId(id);
        entity.setEnabled(enabled);
        thresholdService.updateById(entity);
        return Result.ok();
    }

    @ApiOperation("删除阈值")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@ApiParam("阈值ID") @PathVariable Long id) {
        thresholdService.removeById(id);
        return Result.ok();
    }

    private AlarmThresholdDTO convertToDTO(AlarmThreshold entity) {
        AlarmThresholdDTO dto = new AlarmThresholdDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }
}