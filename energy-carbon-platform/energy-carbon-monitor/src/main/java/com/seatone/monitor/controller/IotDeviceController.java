package com.seatone.monitor.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.seatone.common.result.PageResult;
import com.seatone.common.result.Result;
import com.seatone.monitor.dto.IotDeviceDTO;
import com.seatone.monitor.entity.IotDevice;
import com.seatone.monitor.service.IotDeviceService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Api(tags = "IoT设备管理")
@RestController
@RequestMapping("/iot/device")
public class IotDeviceController {

    @Autowired
    private IotDeviceService deviceService;

    @ApiOperation("分页查询设备列表")
    @GetMapping("/list")
    public Result<PageResult<IotDeviceDTO>> list(
            @ApiParam("页码") @RequestParam(defaultValue = "1") Long pageNum,
            @ApiParam("每页条数") @RequestParam(defaultValue = "10") Long pageSize,
            @ApiParam("设备名称") @RequestParam(required = false) String name,
            @ApiParam("设备编码") @RequestParam(required = false) String code,
            @ApiParam("网关ID") @RequestParam(required = false) Long gatewayId,
            @ApiParam("状态") @RequestParam(required = false) Integer status) {
        Page<IotDevice> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<IotDevice> wrapper = new LambdaQueryWrapper<>();
        if (name != null && !name.isEmpty()) {
            wrapper.like(IotDevice::getDeviceName, name);
        }
        if (code != null && !code.isEmpty()) {
            wrapper.like(IotDevice::getDeviceCode, code);
        }
        if (gatewayId != null) {
            wrapper.eq(IotDevice::getGatewayId, gatewayId);
        }
        if (status != null) {
            wrapper.eq(IotDevice::getStatus, status);
        }
        wrapper.orderByDesc(IotDevice::getCreateTime);
        Page<IotDevice> resultPage = deviceService.page(page, wrapper);
        List<IotDeviceDTO> records = resultPage.getRecords().stream().map(this::convertToDTO).collect(Collectors.toList());
        return Result.ok(PageResult.of(resultPage.getCurrent(), resultPage.getSize(), resultPage.getTotal(), records));
    }

    @ApiOperation("根据ID查询设备")
    @GetMapping("/{id}")
    public Result<IotDeviceDTO> getById(@ApiParam("设备ID") @PathVariable Long id) {
        IotDevice device = deviceService.getById(id);
        if (device == null) {
            return Result.error("设备不存在");
        }
        return Result.ok(convertToDTO(device));
    }

    @ApiOperation("新增设备")
    @PostMapping
    public Result<Void> add(@RequestBody @Valid IotDeviceDTO dto) {
        IotDevice device = new IotDevice();
        BeanUtils.copyProperties(dto, device);
        device.setId(null);
        deviceService.save(device);
        return Result.ok();
    }

    @ApiOperation("修改设备")
    @PutMapping
    public Result<Void> update(@RequestBody IotDeviceDTO dto) {
        IotDevice device = new IotDevice();
        BeanUtils.copyProperties(dto, device);
        deviceService.saveOrUpdate(device);
        return Result.ok();
    }

    @ApiOperation("删除设备")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@ApiParam("设备ID") @PathVariable Long id) {
        deviceService.removeById(id);
        return Result.ok();
    }

    @ApiOperation("获取设备实时数据")
    @GetMapping("/{deviceId}/realtime")
    public Result<Map<String, Number>> realtime(@ApiParam("设备ID") @PathVariable Long deviceId) {
        Map<String, Number> data = new HashMap<>();
        data.put("temperature", 25.6);
        data.put("humidity", 58.3);
        data.put("voltage", 220.0);
        data.put("current", 5.2);
        data.put("power", 1144.0);
        data.put("energy", 10245.8);
        return Result.ok(data);
    }

    private IotDeviceDTO convertToDTO(IotDevice device) {
        IotDeviceDTO dto = new IotDeviceDTO();
        BeanUtils.copyProperties(device, dto);
        return dto;
    }
}
