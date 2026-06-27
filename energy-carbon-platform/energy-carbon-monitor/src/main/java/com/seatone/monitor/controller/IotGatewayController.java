package com.seatone.monitor.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.seatone.common.result.PageResult;
import com.seatone.common.result.Result;
import com.seatone.monitor.dto.IotGatewayDTO;
import com.seatone.monitor.entity.IotGateway;
import com.seatone.monitor.service.IotGatewayService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Api(tags = "IoT网关管理")
@RestController
@RequestMapping("/iot/gateway")
public class IotGatewayController {

    @Autowired
    private IotGatewayService gatewayService;

    @ApiOperation("分页查询网关列表")
    @GetMapping("/list")
    public Result<PageResult<IotGatewayDTO>> list(
            @ApiParam("页码") @RequestParam(defaultValue = "1") Long pageNum,
            @ApiParam("每页条数") @RequestParam(defaultValue = "10") Long pageSize,
            @ApiParam("网关名称") @RequestParam(required = false) String name,
            @ApiParam("网关编码") @RequestParam(required = false) String code,
            @ApiParam("状态") @RequestParam(required = false) Integer status) {
        Page<IotGateway> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<IotGateway> wrapper = new LambdaQueryWrapper<>();
        if (name != null && !name.isEmpty()) {
            wrapper.like(IotGateway::getGatewayName, name);
        }
        if (code != null && !code.isEmpty()) {
            wrapper.like(IotGateway::getGatewayCode, code);
        }
        if (status != null) {
            wrapper.eq(IotGateway::getStatus, status);
        }
        wrapper.orderByDesc(IotGateway::getCreateTime);
        Page<IotGateway> resultPage = gatewayService.page(page, wrapper);
        List<IotGatewayDTO> records = resultPage.getRecords().stream().map(this::convertToDTO).collect(Collectors.toList());
        return Result.ok(PageResult.of(resultPage.getCurrent(), resultPage.getSize(), resultPage.getTotal(), records));
    }

    @ApiOperation("查询所有网关列表")
    @GetMapping("/all")
    public Result<List<IotGatewayDTO>> all() {
        LambdaQueryWrapper<IotGateway> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(IotGateway::getCreateTime);
        List<IotGatewayDTO> list = gatewayService.list(wrapper).stream().map(this::convertToDTO).collect(Collectors.toList());
        return Result.ok(list);
    }

    @ApiOperation("根据ID查询网关")
    @GetMapping("/{id}")
    public Result<IotGatewayDTO> getById(@ApiParam("网关ID") @PathVariable Long id) {
        IotGateway gateway = gatewayService.getById(id);
        if (gateway == null) {
            return Result.error("网关不存在");
        }
        return Result.ok(convertToDTO(gateway));
    }

    @ApiOperation("新增网关")
    @PostMapping
    public Result<Void> add(@RequestBody IotGatewayDTO dto) {
        IotGateway gateway = new IotGateway();
        BeanUtils.copyProperties(dto, gateway);
        gateway.setId(null);
        gatewayService.save(gateway);
        return Result.ok();
    }

    @ApiOperation("修改网关")
    @PutMapping
    public Result<Void> update(@RequestBody IotGatewayDTO dto) {
        IotGateway gateway = new IotGateway();
        BeanUtils.copyProperties(dto, gateway);
        gatewayService.saveOrUpdate(gateway);
        return Result.ok();
    }

    @ApiOperation("删除网关")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@ApiParam("网关ID") @PathVariable Long id) {
        gatewayService.removeById(id);
        return Result.ok();
    }

    private IotGatewayDTO convertToDTO(IotGateway gateway) {
        IotGatewayDTO dto = new IotGatewayDTO();
        BeanUtils.copyProperties(gateway, dto);
        return dto;
    }
}
