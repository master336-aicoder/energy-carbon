package com.seatone.carbon.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.seatone.carbon.dto.CarbonEmissionFactorDTO;
import com.seatone.carbon.entity.CarbonEmissionFactor;
import com.seatone.carbon.service.CarbonEmissionFactorService;
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

@Api(tags = "排放因子库")
@RestController
@RequestMapping("/carbon/factor")
public class CarbonEmissionFactorController {

    @Autowired
    private CarbonEmissionFactorService factorService;

    @ApiOperation("分页查询因子列表")
    @GetMapping("/list")
    public Result<PageResult<CarbonEmissionFactorDTO>> list(
            @ApiParam("页码") @RequestParam(defaultValue = "1") Long current,
            @ApiParam("每页条数") @RequestParam(defaultValue = "10") Long size,
            @ApiParam("因子名称") @RequestParam(required = false) String factorName,
            @ApiParam("因子类别") @RequestParam(required = false) String factorCategory,
            @ApiParam("生效年份") @RequestParam(required = false) Integer effectiveYear) {
        Page<CarbonEmissionFactor> page = new Page<>(current, size);
        LambdaQueryWrapper<CarbonEmissionFactor> wrapper = new LambdaQueryWrapper<>();
        if (factorName != null && !factorName.isEmpty()) {
            wrapper.like(CarbonEmissionFactor::getFactorName, factorName);
        }
        if (factorCategory != null && !factorCategory.isEmpty()) {
            wrapper.eq(CarbonEmissionFactor::getFactorCategory, factorCategory);
        }
        if (effectiveYear != null) {
            wrapper.eq(CarbonEmissionFactor::getEffectiveYear, effectiveYear);
        }
        wrapper.orderByDesc(CarbonEmissionFactor::getCreateTime);
        Page<CarbonEmissionFactor> resultPage = factorService.page(page, wrapper);
        List<CarbonEmissionFactorDTO> records = resultPage.getRecords().stream()
                .map(this::convertToDTO).collect(Collectors.toList());
        return Result.ok(PageResult.of(resultPage.getCurrent(), resultPage.getSize(), resultPage.getTotal(), records));
    }

    @ApiOperation("获取因子详情")
    @GetMapping("/{id}")
    public Result<CarbonEmissionFactorDTO> getById(@ApiParam("因子ID") @PathVariable Long id) {
        CarbonEmissionFactor entity = factorService.getById(id);
        if (entity == null) {
            return Result.error("因子不存在");
        }
        return Result.ok(convertToDTO(entity));
    }

    @ApiOperation("新增排放因子")
    @PostMapping
    public Result<Void> add(@RequestBody CarbonEmissionFactorDTO dto) {
        CarbonEmissionFactor entity = new CarbonEmissionFactor();
        BeanUtils.copyProperties(dto, entity);
        entity.setId(null);
        factorService.save(entity);
        return Result.ok();
    }

    @ApiOperation("修改排放因子")
    @PutMapping("/{id}")
    public Result<Void> update(@ApiParam("因子ID") @PathVariable Long id, @RequestBody CarbonEmissionFactorDTO dto) {
        CarbonEmissionFactor entity = new CarbonEmissionFactor();
        BeanUtils.copyProperties(dto, entity);
        entity.setId(id);
        factorService.updateById(entity);
        return Result.ok();
    }

    @ApiOperation("删除排放因子")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@ApiParam("因子ID") @PathVariable Long id) {
        factorService.removeById(id);
        return Result.ok();
    }

    @ApiOperation("按类别查询因子列表")
    @GetMapping("/category/{category}")
    public Result<List<CarbonEmissionFactorDTO>> getByCategory(
            @ApiParam("因子类别") @PathVariable String category) {
        List<CarbonEmissionFactor> list = factorService.list(
                new LambdaQueryWrapper<CarbonEmissionFactor>()
                        .eq(CarbonEmissionFactor::getFactorCategory, category));
        return Result.ok(list.stream().map(this::convertToDTO).collect(Collectors.toList()));
    }

    @ApiOperation("按能源类型查询因子")
    @GetMapping("/energyType/{energyTypeId}")
    public Result<List<CarbonEmissionFactorDTO>> getByEnergyType(
            @ApiParam("能源类型ID") @PathVariable Long energyTypeId) {
        List<CarbonEmissionFactor> list = factorService.list(
                new LambdaQueryWrapper<CarbonEmissionFactor>()
                        .eq(CarbonEmissionFactor::getEnergyTypeId, energyTypeId));
        return Result.ok(list.stream().map(this::convertToDTO).collect(Collectors.toList()));
    }

    @ApiOperation("获取默认因子列表")
    @GetMapping("/defaults")
    public Result<List<CarbonEmissionFactorDTO>> getDefaults() {
        List<CarbonEmissionFactor> list = factorService.list(
                new LambdaQueryWrapper<CarbonEmissionFactor>()
                        .eq(CarbonEmissionFactor::getDefaultFlag, 1));
        return Result.ok(list.stream().map(this::convertToDTO).collect(Collectors.toList()));
    }

    @ApiOperation("设为默认因子")
    @PutMapping("/{id}/setDefault")
    public Result<Void> setDefault(@ApiParam("因子ID") @PathVariable Long id) {
        CarbonEmissionFactor entity = factorService.getById(id);
        if (entity == null) {
            return Result.error("因子不存在");
        }
        entity.setDefaultFlag(1);
        factorService.updateById(entity);
        return Result.ok();
    }

    private CarbonEmissionFactorDTO convertToDTO(CarbonEmissionFactor entity) {
        CarbonEmissionFactorDTO dto = new CarbonEmissionFactorDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }
}