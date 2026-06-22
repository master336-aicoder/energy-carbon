package com.seatone.carbon.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.seatone.carbon.dto.CarbonAssetDTO;
import com.seatone.carbon.dto.CarbonAssetTradeDTO;
import com.seatone.carbon.entity.CarbonAsset;
import com.seatone.carbon.entity.CarbonAssetTrade;
import com.seatone.carbon.service.CarbonAssetService;
import com.seatone.carbon.service.CarbonAssetTradeService;
import com.seatone.common.result.PageResult;
import com.seatone.common.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Api(tags = "碳资产管理")
@RestController
@RequestMapping("/carbon/asset")
public class CarbonAssetController {

    @Autowired
    private CarbonAssetService assetService;

    @Autowired
    private CarbonAssetTradeService tradeService;

    @ApiOperation("分页查询碳资产列表")
    @GetMapping("/list")
    public Result<PageResult<CarbonAssetDTO>> list(
            @ApiParam("页码") @RequestParam(defaultValue = "1") Long current,
            @ApiParam("每页条数") @RequestParam(defaultValue = "10") Long size,
            @ApiParam("资产类型") @RequestParam(required = false) String assetType,
            @ApiParam("资产名称") @RequestParam(required = false) String assetName,
            @ApiParam("状态") @RequestParam(required = false) String status) {
        Page<CarbonAsset> page = new Page<>(current, size);
        LambdaQueryWrapper<CarbonAsset> wrapper = new LambdaQueryWrapper<>();
        if (assetType != null && !assetType.isEmpty()) {
            wrapper.eq(CarbonAsset::getAssetType, assetType);
        }
        if (assetName != null && !assetName.isEmpty()) {
            wrapper.like(CarbonAsset::getAssetName, assetName);
        }
        if (status != null && !status.isEmpty()) {
            wrapper.eq(CarbonAsset::getStatus, status);
        }
        wrapper.orderByDesc(CarbonAsset::getCreateTime);
        Page<CarbonAsset> resultPage = assetService.page(page, wrapper);
        List<CarbonAssetDTO> records = resultPage.getRecords().stream()
                .map(this::convertToDTO).collect(Collectors.toList());
        return Result.ok(PageResult.of(resultPage.getCurrent(), resultPage.getSize(), resultPage.getTotal(), records));
    }

    @ApiOperation("获取碳资产详情")
    @GetMapping("/{id}")
    public Result<CarbonAssetDTO> getById(@ApiParam("资产ID") @PathVariable Long id) {
        CarbonAsset entity = assetService.getById(id);
        if (entity == null) {
            return Result.error("碳资产记录不存在");
        }
        return Result.ok(convertToDTO(entity));
    }

    @ApiOperation("新增碳资产")
    @PostMapping
    public Result<Void> add(@RequestBody CarbonAssetDTO dto) {
        CarbonAsset entity = new CarbonAsset();
        BeanUtils.copyProperties(dto, entity);
        entity.setId(null);
        entity.setStatus("AVAILABLE");
        assetService.save(entity);
        return Result.ok();
    }

    @ApiOperation("修改碳资产")
    @PutMapping("/{id}")
    public Result<Void> update(@ApiParam("资产ID") @PathVariable Long id, @RequestBody CarbonAssetDTO dto) {
        CarbonAsset entity = new CarbonAsset();
        BeanUtils.copyProperties(dto, entity);
        entity.setId(id);
        assetService.updateById(entity);
        return Result.ok();
    }

    @ApiOperation("删除碳资产")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@ApiParam("资产ID") @PathVariable Long id) {
        assetService.removeById(id);
        return Result.ok();
    }

    @ApiOperation("资产汇总统计（按类型统计总量/总价值）")
    @GetMapping("/summary")
    public Result<List<Map<String, Object>>> summary() {
        List<CarbonAsset> allAssets = assetService.list();
        Map<String, BigDecimal> quantityMap = new HashMap<>();
        Map<String, BigDecimal> costMap = new HashMap<>();
        Map<String, Long> countMap = new HashMap<>();

        for (CarbonAsset asset : allAssets) {
            String type = asset.getAssetType();
            quantityMap.merge(type, asset.getQuantity() != null ? asset.getQuantity() : BigDecimal.ZERO, BigDecimal::add);
            costMap.merge(type, asset.getCost() != null ? asset.getCost() : BigDecimal.ZERO, BigDecimal::add);
            countMap.merge(type, 1L, Long::sum);
        }

        List<Map<String, Object>> result = new ArrayList<>();
        String[] types = {"QUOTA", "CCER", "GREEN_CERT"};
        String[] typeNames = {"配额", "CCER", "绿证"};
        for (int i = 0; i < types.length; i++) {
            Map<String, Object> item = new HashMap<>();
            item.put("assetType", types[i]);
            item.put("assetTypeName", typeNames[i]);
            item.put("totalQuantity", quantityMap.getOrDefault(types[i], BigDecimal.ZERO));
            item.put("totalCost", costMap.getOrDefault(types[i], BigDecimal.ZERO));
            item.put("count", countMap.getOrDefault(types[i], 0L));
            result.add(item);
        }

        // 总计
        BigDecimal totalQuantity = allAssets.stream()
                .map(a -> a.getQuantity() != null ? a.getQuantity() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalCost = allAssets.stream()
                .map(a -> a.getCost() != null ? a.getCost() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        long availableCount = allAssets.stream().filter(a -> "AVAILABLE".equals(a.getStatus())).count();
        long usedCount = allAssets.stream().filter(a -> "USED".equals(a.getStatus())).count();
        long frozenCount = allAssets.stream().filter(a -> "FROZEN".equals(a.getStatus())).count();

        Map<String, Object> total = new HashMap<>();
        total.put("assetType", "TOTAL");
        total.put("assetTypeName", "总计");
        total.put("totalQuantity", totalQuantity);
        total.put("totalCost", totalCost);
        total.put("count", allAssets.size());
        total.put("availableCount", availableCount);
        total.put("usedCount", usedCount);
        total.put("frozenCount", frozenCount);
        result.add(total);

        return Result.ok(result);
    }

    @ApiOperation("查询交易记录")
    @GetMapping("/trade/list")
    public Result<PageResult<CarbonAssetTradeDTO>> tradeList(
            @ApiParam("页码") @RequestParam(defaultValue = "1") Long current,
            @ApiParam("每页条数") @RequestParam(defaultValue = "10") Long size,
            @ApiParam("碳资产ID") @RequestParam(required = false) Long assetId,
            @ApiParam("交易类型") @RequestParam(required = false) String tradeType) {
        Page<CarbonAssetTrade> page = new Page<>(current, size);
        LambdaQueryWrapper<CarbonAssetTrade> wrapper = new LambdaQueryWrapper<>();
        if (assetId != null) {
            wrapper.eq(CarbonAssetTrade::getAssetId, assetId);
        }
        if (tradeType != null && !tradeType.isEmpty()) {
            wrapper.eq(CarbonAssetTrade::getTradeType, tradeType);
        }
        wrapper.orderByDesc(CarbonAssetTrade::getCreateTime);
        Page<CarbonAssetTrade> resultPage = tradeService.page(page, wrapper);
        List<CarbonAssetTradeDTO> records = resultPage.getRecords().stream()
                .map(this::convertTradeToDTO).collect(Collectors.toList());
        return Result.ok(PageResult.of(resultPage.getCurrent(), resultPage.getSize(), resultPage.getTotal(), records));
    }

    @ApiOperation("新增交易记录")
    @PostMapping("/trade")
    public Result<Void> addTrade(@RequestBody CarbonAssetTradeDTO dto) {
        CarbonAssetTrade entity = new CarbonAssetTrade();
        BeanUtils.copyProperties(dto, entity);
        entity.setId(null);
        // 计算交易金额
        if (entity.getTradeQuantity() != null && entity.getTradePrice() != null) {
            entity.setTradeAmount(entity.getTradeQuantity().multiply(entity.getTradePrice()));
        }
        tradeService.save(entity);

        // 更新碳资产数量
        if (dto.getAssetId() != null) {
            CarbonAsset asset = assetService.getById(dto.getAssetId());
            if (asset != null && entity.getTradeQuantity() != null) {
                if ("BUY".equals(dto.getTradeType())) {
                    asset.setQuantity(asset.getQuantity().add(entity.getTradeQuantity()));
                } else if ("SELL".equals(dto.getTradeType())) {
                    asset.setQuantity(asset.getQuantity().subtract(entity.getTradeQuantity()));
                }
                assetService.updateById(asset);
            }
        }
        return Result.ok();
    }

    private CarbonAssetDTO convertToDTO(CarbonAsset entity) {
        CarbonAssetDTO dto = new CarbonAssetDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }

    private CarbonAssetTradeDTO convertTradeToDTO(CarbonAssetTrade entity) {
        CarbonAssetTradeDTO dto = new CarbonAssetTradeDTO();
        BeanUtils.copyProperties(entity, dto);
        // 填充资产名称
        if (entity.getAssetId() != null) {
            CarbonAsset asset = assetService.getById(entity.getAssetId());
            if (asset != null) {
                dto.setAssetName(asset.getAssetName());
            }
        }
        return dto;
    }
}
