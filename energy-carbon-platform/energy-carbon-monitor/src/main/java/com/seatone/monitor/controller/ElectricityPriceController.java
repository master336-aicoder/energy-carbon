package com.seatone.monitor.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.seatone.common.result.PageResult;
import com.seatone.common.result.Result;
import com.seatone.monitor.dto.ElectricityPriceDTO;
import com.seatone.monitor.entity.ToolkitElectricityPrice;
import com.seatone.monitor.service.ToolkitElectricityPriceService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Api(tags = "分时电价管理")
@RestController
@RequestMapping("/toolkit/electricity-price")
public class ElectricityPriceController {

    @Autowired
    private ToolkitElectricityPriceService electricityPriceService;

    @ApiOperation("分页查询电价配置列表")
    @GetMapping("/list")
    public Result<PageResult<ElectricityPriceDTO>> list(
            @ApiParam("页码") @RequestParam(defaultValue = "1") Long current,
            @ApiParam("每页条数") @RequestParam(defaultValue = "10") Long size) {
        Page<ToolkitElectricityPrice> page = new Page<>(current, size);
        LambdaQueryWrapper<ToolkitElectricityPrice> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ToolkitElectricityPrice::getDeleted, 0);
        wrapper.orderByDesc(ToolkitElectricityPrice::getEffectiveDate);
        Page<ToolkitElectricityPrice> resultPage = electricityPriceService.page(page, wrapper);
        List<ElectricityPriceDTO> records = resultPage.getRecords().stream().map(this::convertToDTO).collect(Collectors.toList());
        return Result.ok(PageResult.of(resultPage.getCurrent(), resultPage.getSize(), resultPage.getTotal(), records));
    }

    @ApiOperation("获取当前生效电价")
    @GetMapping("/current")
    public Result<ElectricityPriceDTO> current() {
        LambdaQueryWrapper<ToolkitElectricityPrice> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ToolkitElectricityPrice::getDeleted, 0);
        wrapper.eq(ToolkitElectricityPrice::getStatus, 1);
        wrapper.le(ToolkitElectricityPrice::getEffectiveDate, java.time.LocalDate.now());
        wrapper.orderByDesc(ToolkitElectricityPrice::getEffectiveDate);
        wrapper.last("LIMIT 1");
        ToolkitElectricityPrice entity = electricityPriceService.getOne(wrapper);
        if (entity == null) {
            return Result.error("暂无生效电价配置");
        }
        return Result.ok(convertToDTO(entity));
    }

    @ApiOperation("获取电价详情")
    @GetMapping("/{id}")
    public Result<ElectricityPriceDTO> getById(@ApiParam("电价ID") @PathVariable Long id) {
        ToolkitElectricityPrice entity = electricityPriceService.getById(id);
        if (entity == null) {
            return Result.error("电价配置不存在");
        }
        return Result.ok(convertToDTO(entity));
    }

    @ApiOperation("新增电价配置")
    @PostMapping
    public Result<Void> add(@RequestBody ToolkitElectricityPrice price) {
        price.setDeleted(0);
        electricityPriceService.save(price);
        return Result.ok();
    }

    @ApiOperation("修改电价配置")
    @PutMapping("/{id}")
    public Result<Void> update(@ApiParam("电价ID") @PathVariable Long id, @RequestBody ToolkitElectricityPrice price) {
        price.setId(id);
        electricityPriceService.updateById(price);
        return Result.ok();
    }

    @ApiOperation("启用/禁用电价")
    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(@ApiParam("电价ID") @PathVariable Long id,
                                      @ApiParam("状态") @RequestParam Integer status) {
        ToolkitElectricityPrice price = new ToolkitElectricityPrice();
        price.setId(id);
        price.setStatus(status);
        electricityPriceService.updateById(price);
        return Result.ok();
    }

    @ApiOperation("删除电价配置")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@ApiParam("电价ID") @PathVariable Long id) {
        ToolkitElectricityPrice price = new ToolkitElectricityPrice();
        price.setId(id);
        price.setDeleted(1);
        electricityPriceService.updateById(price);
        return Result.ok();
    }

    private ElectricityPriceDTO convertToDTO(ToolkitElectricityPrice entity) {
        ElectricityPriceDTO dto = new ElectricityPriceDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }
}
