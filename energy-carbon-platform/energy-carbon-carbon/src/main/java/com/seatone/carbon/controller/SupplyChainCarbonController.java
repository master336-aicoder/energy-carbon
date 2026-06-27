package com.seatone.carbon.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.seatone.carbon.dto.SupplyChainCarbonDTO;
import com.seatone.carbon.entity.SupplyChainCarbon;
import com.seatone.carbon.service.SupplyChainCarbonService;
import com.seatone.common.result.PageResult;
import com.seatone.common.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Api(tags = "供应链碳足迹管理")
@RestController
@RequestMapping("/carbon/supply-chain")
public class SupplyChainCarbonController {

    @Autowired
    private SupplyChainCarbonService supplyChainService;

    @ApiOperation("分页查询供应链碳足迹列表")
    @GetMapping("/list")
    public Result<PageResult<SupplyChainCarbonDTO>> list(
            @ApiParam("页码") @RequestParam(defaultValue = "1") Long current,
            @ApiParam("每页条数") @RequestParam(defaultValue = "10") Long size,
            @ApiParam("供应商名称") @RequestParam(required = false) String supplierName,
            @ApiParam("产品名称") @RequestParam(required = false) String productName,
            @ApiParam("产品类别") @RequestParam(required = false) String productCategory) {
        Page<SupplyChainCarbon> page = new Page<>(current, size);
        LambdaQueryWrapper<SupplyChainCarbon> wrapper = new LambdaQueryWrapper<>();
        if (supplierName != null && !supplierName.isEmpty()) {
            wrapper.like(SupplyChainCarbon::getSupplierName, supplierName);
        }
        if (productName != null && !productName.isEmpty()) {
            wrapper.like(SupplyChainCarbon::getProductName, productName);
        }
        if (productCategory != null && !productCategory.isEmpty()) {
            wrapper.eq(SupplyChainCarbon::getProductCategory, productCategory);
        }
        wrapper.orderByDesc(SupplyChainCarbon::getCreateTime);
        Page<SupplyChainCarbon> resultPage = supplyChainService.page(page, wrapper);
        List<SupplyChainCarbonDTO> records = resultPage.getRecords().stream()
                .map(this::convertToDTO).collect(Collectors.toList());
        return Result.ok(PageResult.of(resultPage.getCurrent(), resultPage.getSize(), resultPage.getTotal(), records));
    }

    @ApiOperation("获取供应链碳足迹详情")
    @GetMapping("/{id}")
    public Result<SupplyChainCarbonDTO> getById(@ApiParam("记录ID") @PathVariable Long id) {
        SupplyChainCarbon entity = supplyChainService.getById(id);
        if (entity == null) {
            return Result.error("记录不存在");
        }
        return Result.ok(convertToDTO(entity));
    }

    @ApiOperation("新增供应链碳足迹")
    @PostMapping
    public Result<Void> add(@RequestBody @Valid SupplyChainCarbonDTO dto) {
        SupplyChainCarbon entity = new SupplyChainCarbon();
        BeanUtils.copyProperties(dto, entity);
        entity.setId(null);
        entity.setStatus(1);
        supplyChainService.save(entity);
        return Result.ok();
    }

    @ApiOperation("修改供应链碳足迹")
    @PutMapping("/{id}")
    public Result<Void> update(@ApiParam("记录ID") @PathVariable Long id, @RequestBody SupplyChainCarbonDTO dto) {
        SupplyChainCarbon entity = new SupplyChainCarbon();
        BeanUtils.copyProperties(dto, entity);
        entity.setId(id);
        supplyChainService.updateById(entity);
        return Result.ok();
    }

    @ApiOperation("删除供应链碳足迹")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@ApiParam("记录ID") @PathVariable Long id) {
        supplyChainService.removeById(id);
        return Result.ok();
    }

    @ApiOperation("按供应商汇总统计")
    @GetMapping("/summary/supplier")
    public Result<List<Map<String, Object>>> summaryBySupplier() {
        List<SupplyChainCarbon> allList = supplyChainService.list();
        Map<String, List<SupplyChainCarbon>> groupBySupplier = allList.stream()
                .collect(Collectors.groupingBy(SupplyChainCarbon::getSupplierName));

        List<Map<String, Object>> result = new ArrayList<>();
        for (Map.Entry<String, List<SupplyChainCarbon>> entry : groupBySupplier.entrySet()) {
            Map<String, Object> item = new HashMap<>();
            item.put("supplierName", entry.getKey());
            item.put("productCount", entry.getValue().size());
            BigDecimal avgFootprint = entry.getValue().stream()
                    .map(s -> s.getCarbonFootprintValue() != null ? s.getCarbonFootprintValue() : BigDecimal.ZERO)
                    .reduce(BigDecimal.ZERO, BigDecimal::add)
                    .divide(BigDecimal.valueOf(Math.max(entry.getValue().size(), 1)), 4, java.math.RoundingMode.HALF_UP);
            item.put("avgCarbonFootprint", avgFootprint);
            result.add(item);
        }
        return Result.ok(result);
    }

    @ApiOperation("按产品汇总统计")
    @GetMapping("/summary/product")
    public Result<List<Map<String, Object>>> summaryByProduct() {
        List<SupplyChainCarbon> allList = supplyChainService.list();
        Map<String, List<SupplyChainCarbon>> groupByProduct = allList.stream()
                .collect(Collectors.groupingBy(SupplyChainCarbon::getProductName));

        List<Map<String, Object>> result = new ArrayList<>();
        for (Map.Entry<String, List<SupplyChainCarbon>> entry : groupByProduct.entrySet()) {
            Map<String, Object> item = new HashMap<>();
            item.put("productName", entry.getKey());
            item.put("supplierCount", entry.getValue().stream()
                    .map(SupplyChainCarbon::getSupplierName).distinct().count());
            BigDecimal avgFootprint = entry.getValue().stream()
                    .map(s -> s.getCarbonFootprintValue() != null ? s.getCarbonFootprintValue() : BigDecimal.ZERO)
                    .reduce(BigDecimal.ZERO, BigDecimal::add)
                    .divide(BigDecimal.valueOf(Math.max(entry.getValue().size(), 1)), 4, java.math.RoundingMode.HALF_UP);
            item.put("avgCarbonFootprint", avgFootprint);
            result.add(item);
        }
        return Result.ok(result);
    }

    private SupplyChainCarbonDTO convertToDTO(SupplyChainCarbon entity) {
        SupplyChainCarbonDTO dto = new SupplyChainCarbonDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }
}
