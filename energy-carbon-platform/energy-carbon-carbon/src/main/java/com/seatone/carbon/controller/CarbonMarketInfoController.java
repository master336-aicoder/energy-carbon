package com.seatone.carbon.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.seatone.carbon.dto.CarbonMarketInfoDTO;
import com.seatone.carbon.entity.CarbonMarketInfo;
import com.seatone.carbon.service.CarbonMarketInfoService;
import com.seatone.common.result.PageResult;
import com.seatone.common.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import java.util.List;
import java.util.stream.Collectors;

@Api(tags = "市场供需信息")
@RestController
@RequestMapping("/carbon/market")
public class CarbonMarketInfoController {

    @Autowired
    private CarbonMarketInfoService marketInfoService;

    @ApiOperation("分页查询市场信息列表")
    @GetMapping("/list")
    public Result<PageResult<CarbonMarketInfoDTO>> list(
            @ApiParam("页码") @RequestParam(defaultValue = "1") Long current,
            @ApiParam("每页条数") @RequestParam(defaultValue = "10") Long size,
            @ApiParam("信息分类") @RequestParam(required = false) String infoCategory,
            @ApiParam("信息标题") @RequestParam(required = false) String infoTitle) {
        Page<CarbonMarketInfo> page = new Page<>(current, size);
        LambdaQueryWrapper<CarbonMarketInfo> wrapper = new LambdaQueryWrapper<>();
        if (infoCategory != null && !infoCategory.isEmpty()) {
            wrapper.eq(CarbonMarketInfo::getInfoCategory, infoCategory);
        }
        if (infoTitle != null && !infoTitle.isEmpty()) {
            wrapper.like(CarbonMarketInfo::getInfoTitle, infoTitle);
        }
        wrapper.orderByDesc(CarbonMarketInfo::getPublishDate);
        Page<CarbonMarketInfo> resultPage = marketInfoService.page(page, wrapper);
        List<CarbonMarketInfoDTO> records = resultPage.getRecords().stream()
                .map(this::convertToDTO).collect(Collectors.toList());
        return Result.ok(PageResult.of(resultPage.getCurrent(), resultPage.getSize(), resultPage.getTotal(), records));
    }

    @ApiOperation("获取市场信息详情")
    @GetMapping("/{id}")
    public Result<CarbonMarketInfoDTO> getById(@ApiParam("信息ID") @PathVariable Long id) {
        CarbonMarketInfo entity = marketInfoService.getById(id);
        if (entity == null) {
            return Result.error("信息记录不存在");
        }
        return Result.ok(convertToDTO(entity));
    }

    @ApiOperation("发布市场信息")
    @PostMapping
    public Result<Void> add(@RequestBody @Valid CarbonMarketInfoDTO dto) {
        CarbonMarketInfo entity = new CarbonMarketInfo();
        BeanUtils.copyProperties(dto, entity);
        entity.setId(null);
        entity.setStatus(1);
        marketInfoService.save(entity);
        return Result.ok();
    }

    @ApiOperation("修改市场信息")
    @PutMapping("/{id}")
    public Result<Void> update(@ApiParam("信息ID") @PathVariable Long id, @RequestBody CarbonMarketInfoDTO dto) {
        CarbonMarketInfo entity = new CarbonMarketInfo();
        BeanUtils.copyProperties(dto, entity);
        entity.setId(id);
        marketInfoService.updateById(entity);
        return Result.ok();
    }

    @ApiOperation("删除市场信息")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@ApiParam("信息ID") @PathVariable Long id) {
        marketInfoService.removeById(id);
        return Result.ok();
    }

    @ApiOperation("获取最新市场信息")
    @GetMapping("/latest")
    public Result<List<CarbonMarketInfoDTO>> latest(
            @ApiParam("信息分类") @RequestParam(required = false) String infoCategory,
            @ApiParam("数量限制") @RequestParam(defaultValue = "5") Integer limit) {
        LambdaQueryWrapper<CarbonMarketInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CarbonMarketInfo::getStatus, 1);
        if (infoCategory != null && !infoCategory.isEmpty()) {
            wrapper.eq(CarbonMarketInfo::getInfoCategory, infoCategory);
        }
        wrapper.orderByDesc(CarbonMarketInfo::getPublishDate);
        wrapper.last("LIMIT " + limit);
        List<CarbonMarketInfo> list = marketInfoService.list(wrapper);
        List<CarbonMarketInfoDTO> records = list.stream()
                .map(this::convertToDTO).collect(Collectors.toList());
        return Result.ok(records);
    }

    private CarbonMarketInfoDTO convertToDTO(CarbonMarketInfo entity) {
        CarbonMarketInfoDTO dto = new CarbonMarketInfoDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }
}
