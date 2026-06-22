package com.seatone.carbon.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.seatone.carbon.dto.*;
import com.seatone.carbon.entity.*;
import com.seatone.carbon.service.*;
import com.seatone.common.result.PageResult;
import com.seatone.common.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

@Api(tags = "碳排放核算")
@RestController
@RequestMapping("/carbon/accounting")
public class CarbonAccountingController {

    @Autowired
    private CarbonAccountingService accountingService;

    @Autowired
    private CarbonAccountingDetailService detailService;

    @Autowired
    private CarbonEmissionFactorService factorService;

    @ApiOperation("分页查询核算列表")
    @GetMapping("/list")
    public Result<PageResult<CarbonAccountingDTO>> list(
            @ApiParam("页码") @RequestParam(defaultValue = "1") Long current,
            @ApiParam("每页条数") @RequestParam(defaultValue = "10") Long size,
            @ApiParam("核算名称") @RequestParam(required = false) String accountingName,
            @ApiParam("核算年份") @RequestParam(required = false) Integer accountingYear,
            @ApiParam("核算期次") @RequestParam(required = false) String accountingPeriod,
            @ApiParam("状态") @RequestParam(required = false) String status) {
        Page<CarbonAccounting> page = new Page<>(current, size);
        LambdaQueryWrapper<CarbonAccounting> wrapper = new LambdaQueryWrapper<>();
        if (accountingName != null && !accountingName.isEmpty()) {
            wrapper.like(CarbonAccounting::getAccountingName, accountingName);
        }
        if (accountingYear != null) {
            wrapper.eq(CarbonAccounting::getAccountingYear, accountingYear);
        }
        if (accountingPeriod != null && !accountingPeriod.isEmpty()) {
            wrapper.eq(CarbonAccounting::getAccountingPeriod, accountingPeriod);
        }
        if (status != null && !status.isEmpty()) {
            wrapper.eq(CarbonAccounting::getStatus, status);
        }
        wrapper.orderByDesc(CarbonAccounting::getCreateTime);
        Page<CarbonAccounting> resultPage = accountingService.page(page, wrapper);
        List<CarbonAccountingDTO> records = resultPage.getRecords().stream()
                .map(this::convertToDTO).collect(Collectors.toList());
        return Result.ok(PageResult.of(resultPage.getCurrent(), resultPage.getSize(), resultPage.getTotal(), records));
    }

    @ApiOperation("获取核算详情（含明细）")
    @GetMapping("/{id}")
    public Result<CarbonAccountingDTO> getById(@ApiParam("核算ID") @PathVariable Long id) {
        CarbonAccounting entity = accountingService.getById(id);
        if (entity == null) {
            return Result.error("核算记录不存在");
        }
        CarbonAccountingDTO dto = convertToDTO(entity);
        // 查询明细
        List<CarbonAccountingDetail> details = detailService.list(
                new LambdaQueryWrapper<CarbonAccountingDetail>()
                        .eq(CarbonAccountingDetail::getAccountingId, id));
        dto.setDetails(details.stream().map(this::convertDetailToDTO).collect(Collectors.toList()));
        return Result.ok(dto);
    }

    @ApiOperation("新增核算")
    @PostMapping
    public Result<Void> add(@RequestBody CarbonAccountingDTO dto) {
        CarbonAccounting entity = new CarbonAccounting();
        BeanUtils.copyProperties(dto, entity);
        entity.setId(null);
        entity.setStatus("DRAFT");
        accountingService.save(entity);
        return Result.ok();
    }

    @ApiOperation("修改核算")
    @PutMapping("/{id}")
    public Result<Void> update(@ApiParam("核算ID") @PathVariable Long id, @RequestBody CarbonAccountingDTO dto) {
        CarbonAccounting entity = new CarbonAccounting();
        BeanUtils.copyProperties(dto, entity);
        entity.setId(id);
        accountingService.updateById(entity);
        return Result.ok();
    }

    @ApiOperation("删除核算")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@ApiParam("核算ID") @PathVariable Long id) {
        accountingService.removeById(id);
        return Result.ok();
    }

    @ApiOperation("提交核算")
    @PutMapping("/{id}/submit")
    public Result<Void> submit(@ApiParam("核算ID") @PathVariable Long id) {
        CarbonAccounting entity = accountingService.getById(id);
        if (entity == null) {
            return Result.error("核算记录不存在");
        }
        entity.setStatus("SUBMITTED");
        accountingService.updateById(entity);
        return Result.ok();
    }

    @ApiOperation("审核核算")
    @PutMapping("/{id}/approve")
    public Result<Void> approve(@ApiParam("核算ID") @PathVariable Long id) {
        CarbonAccounting entity = accountingService.getById(id);
        if (entity == null) {
            return Result.error("核算记录不存在");
        }
        entity.setStatus("APPROVED");
        accountingService.updateById(entity);
        return Result.ok();
    }

    @ApiOperation("排放计算（活动数据 x 排放因子）")
    @PostMapping("/calculate")
    public Result<Void> calculate(@RequestBody EmissionCalcDTO calcDTO) {
        // 查询核算
        CarbonAccounting accounting = accountingService.getById(calcDTO.getAccountingId());
        if (accounting == null) {
            return Result.error("核算记录不存在");
        }

        BigDecimal totalEmission = BigDecimal.ZERO;

        for (EmissionCalcDTO.ActivityDataItem item : calcDTO.getActivities()) {
            CarbonAccountingDetail detail = new CarbonAccountingDetail();
            detail.setAccountingId(calcDTO.getAccountingId());
            detail.setActivityName(item.getActivityName());
            detail.setEnergyTypeId(item.getEnergyTypeId());
            detail.setActivityData(item.getActivityData());
            detail.setActivityUnit(item.getActivityUnit());
            detail.setSourceDescription(item.getSourceDescription());

            BigDecimal factorValue;
            if (item.getFactorId() != null) {
                // 使用指定的排放因子
                CarbonEmissionFactor factor = factorService.getById(item.getFactorId());
                if (factor != null) {
                    factorValue = factor.getFactorValue();
                    detail.setEmissionFactor(factorValue);
                    detail.setFactorUnit(factor.getFactorUnit());
                } else {
                    factorValue = BigDecimal.ZERO;
                }
            } else if (item.getEnergyTypeId() != null) {
                // 使用能源类型关联的默认因子
                LambdaQueryWrapper<CarbonEmissionFactor> fw = new LambdaQueryWrapper<>();
                fw.eq(CarbonEmissionFactor::getEnergyTypeId, item.getEnergyTypeId())
                        .eq(CarbonEmissionFactor::getDefaultFlag, 1);
                CarbonEmissionFactor factor = factorService.getOne(fw);
                if (factor != null) {
                    factorValue = factor.getFactorValue();
                    detail.setEmissionFactor(factorValue);
                    detail.setFactorUnit(factor.getFactorUnit());
                } else {
                    factorValue = BigDecimal.ZERO;
                }
            } else {
                factorValue = BigDecimal.ZERO;
            }

            // 排放量 = 活动数据 x 排放因子
            BigDecimal emission = BigDecimal.ZERO;
            if (item.getActivityData() != null && factorValue != null) {
                emission = item.getActivityData().multiply(factorValue).setScale(4, RoundingMode.HALF_UP);
            }
            detail.setEmission(emission);
            detail.setEmissionUnit("tCO2e");

            totalEmission = totalEmission.add(emission);
            detailService.save(detail);
        }

        // 更新核算主表总排放量
        accounting.setTotalEmission(totalEmission.setScale(4, RoundingMode.HALF_UP));
        accounting.setEmissionUnit("tCO2e");
        accountingService.updateById(accounting);

        return Result.ok();
    }

    @ApiOperation("获取排放报告数据")
    @GetMapping("/report/{id}")
    public Result<EmissionReportDTO> getReport(@ApiParam("核算ID") @PathVariable Long id) {
        CarbonAccounting accounting = accountingService.getById(id);
        if (accounting == null) {
            return Result.error("核算记录不存在");
        }
        List<CarbonAccountingDetail> details = detailService.list(
                new LambdaQueryWrapper<CarbonAccountingDetail>()
                        .eq(CarbonAccountingDetail::getAccountingId, id));

        EmissionReportDTO report = new EmissionReportDTO();
        report.setAccountingId(accounting.getId());
        report.setAccountingName(accounting.getAccountingName());
        report.setAccountingYear(accounting.getAccountingYear());
        report.setAccountingPeriod(accounting.getAccountingPeriod());

        BigDecimal scope1 = BigDecimal.ZERO;
        BigDecimal scope2 = BigDecimal.ZERO;
        BigDecimal scope3 = BigDecimal.ZERO;

        for (CarbonAccountingDetail d : details) {
            if (d.getEmission() != null) {
                // 根据关联能源类型判断范围（简化逻辑：需要查能源类型表）
                // 这里简化为全部计入范围一，实际应按能源类型分类
                scope1 = scope1.add(d.getEmission());
            }
        }

        report.setScope1Emission(scope1);
        report.setScope2Emission(scope2);
        report.setScope3Emission(scope3);

        BigDecimal total = scope1.add(scope2).add(scope3);
        report.setTotalEmission(total);
        report.setEmissionUnit("tCO2e");

        if (total.compareTo(BigDecimal.ZERO) > 0) {
            report.setScope1Percent(scope1.multiply(BigDecimal.valueOf(100)).divide(total, 2, RoundingMode.HALF_UP));
            report.setScope2Percent(scope2.multiply(BigDecimal.valueOf(100)).divide(total, 2, RoundingMode.HALF_UP));
            report.setScope3Percent(scope3.multiply(BigDecimal.valueOf(100)).divide(total, 2, RoundingMode.HALF_UP));
        } else {
            report.setScope1Percent(BigDecimal.ZERO);
            report.setScope2Percent(BigDecimal.ZERO);
            report.setScope3Percent(BigDecimal.ZERO);
        }

        return Result.ok(report);
    }

    private CarbonAccountingDTO convertToDTO(CarbonAccounting entity) {
        CarbonAccountingDTO dto = new CarbonAccountingDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }

    private CarbonAccountingDetailDTO convertDetailToDTO(CarbonAccountingDetail entity) {
        CarbonAccountingDetailDTO dto = new CarbonAccountingDetailDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }
}