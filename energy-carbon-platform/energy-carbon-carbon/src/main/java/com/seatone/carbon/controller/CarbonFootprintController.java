package com.seatone.carbon.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.seatone.carbon.dto.CarbonFootprintProductDTO;
import com.seatone.carbon.entity.CarbonFootprintProduct;
import com.seatone.carbon.service.CarbonFootprintProductService;
import com.seatone.common.result.PageResult;
import com.seatone.common.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Api(tags = "产品碳足迹")
@RestController
@RequestMapping("/carbon/footprint")
public class CarbonFootprintController {

    @Autowired
    private CarbonFootprintProductService footprintService;

    @ApiOperation("分页查询碳足迹")
    @GetMapping("/list")
    public Result<PageResult<CarbonFootprintProductDTO>> list(
            @ApiParam("页码") @RequestParam(defaultValue = "1") Long current,
            @ApiParam("每页条数") @RequestParam(defaultValue = "10") Long size,
            @ApiParam("产品ID") @RequestParam(required = false) Long productId) {
        Page<CarbonFootprintProduct> page = new Page<>(current, size);
        LambdaQueryWrapper<CarbonFootprintProduct> wrapper = new LambdaQueryWrapper<>();
        if (productId != null) {
            wrapper.eq(CarbonFootprintProduct::getProductId, productId);
        }
        wrapper.orderByDesc(CarbonFootprintProduct::getCreateTime);
        Page<CarbonFootprintProduct> resultPage = footprintService.page(page, wrapper);
        List<CarbonFootprintProductDTO> records = resultPage.getRecords().stream()
                .map(this::convertToDTO).collect(Collectors.toList());
        return Result.ok(PageResult.of(resultPage.getCurrent(), resultPage.getSize(), resultPage.getTotal(), records));
    }

    @ApiOperation("获取产品碳足迹详情")
    @GetMapping("/{id}")
    public Result<CarbonFootprintProductDTO> getById(@ApiParam("足迹ID") @PathVariable Long id) {
        CarbonFootprintProduct entity = footprintService.getById(id);
        if (entity == null) {
            return Result.error("足迹记录不存在");
        }
        return Result.ok(convertToDTO(entity));
    }

    @ApiOperation("新增碳足迹记录")
    @PostMapping
    public Result<Void> add(@RequestBody CarbonFootprintProductDTO dto) {
        CarbonFootprintProduct entity = new CarbonFootprintProduct();
        BeanUtils.copyProperties(dto, entity);
        entity.setId(null);
        footprintService.save(entity);
        return Result.ok();
    }

    @ApiOperation("修改碳足迹记录")
    @PutMapping("/{id}")
    public Result<Void> update(@ApiParam("足迹ID") @PathVariable Long id, @RequestBody CarbonFootprintProductDTO dto) {
        CarbonFootprintProduct entity = new CarbonFootprintProduct();
        BeanUtils.copyProperties(dto, entity);
        entity.setId(id);
        footprintService.updateById(entity);
        return Result.ok();
    }

    @ApiOperation("删除碳足迹记录")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@ApiParam("足迹ID") @PathVariable Long id) {
        footprintService.removeById(id);
        return Result.ok();
    }

    @ApiOperation("按产品查询碳足迹")
    @GetMapping("/product/{productId}")
    public Result<List<CarbonFootprintProductDTO>> getByProduct(
            @ApiParam("产品ID") @PathVariable Long productId) {
        List<CarbonFootprintProduct> list = footprintService.list(
                new LambdaQueryWrapper<CarbonFootprintProduct>()
                        .eq(CarbonFootprintProduct::getProductId, productId));
        return Result.ok(list.stream().map(this::convertToDTO).collect(Collectors.toList()));
    }

    private CarbonFootprintProductDTO convertToDTO(CarbonFootprintProduct entity) {
        CarbonFootprintProductDTO dto = new CarbonFootprintProductDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }
}