package com.seatone.monitor.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.seatone.common.result.Result;
import com.seatone.monitor.dto.IotDeviceDTO;
import com.seatone.monitor.entity.IotDevice;
import com.seatone.monitor.service.IotDeviceService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@Api(tags = "IoT网关设备管理")
@RestController
@RequestMapping("/iot/gateway")
public class IotGatewayDeviceController {

    @Autowired
    private IotDeviceService deviceService;

    @ApiOperation("获取网关下的设备列表")
    @GetMapping("/{gatewayId}/devices")
    public Result<List<IotDeviceDTO>> getDevicesByGatewayId(@ApiParam("网关ID") @PathVariable Long gatewayId) {
        LambdaQueryWrapper<IotDevice> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(IotDevice::getGatewayId, gatewayId);
        wrapper.orderByDesc(IotDevice::getCreateTime);
        List<IotDeviceDTO> list = deviceService.list(wrapper).stream().map(this::convertToDTO).collect(Collectors.toList());
        return Result.ok(list);
    }

    private IotDeviceDTO convertToDTO(IotDevice device) {
        IotDeviceDTO dto = new IotDeviceDTO();
        BeanUtils.copyProperties(device, dto);
        return dto;
    }
}
