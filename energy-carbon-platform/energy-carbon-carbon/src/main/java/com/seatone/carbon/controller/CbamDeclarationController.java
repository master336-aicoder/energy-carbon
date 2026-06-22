package com.seatone.carbon.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.seatone.carbon.dto.CbamDeclarationDTO;
import com.seatone.carbon.dto.CbamDeclarationDetailDTO;
import com.seatone.carbon.entity.CbamDeclaration;
import com.seatone.carbon.entity.CbamDeclarationDetail;
import com.seatone.carbon.service.CbamDeclarationDetailService;
import com.seatone.carbon.service.CbamDeclarationService;
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

@Api(tags = "CBAM申报管理")
@RestController
@RequestMapping("/carbon/cbam")
public class CbamDeclarationController {

    @Autowired
    private CbamDeclarationService declarationService;

    @Autowired
    private CbamDeclarationDetailService detailService;

    @ApiOperation("分页查询CBAM申报列表")
    @GetMapping("/list")
    public Result<PageResult<CbamDeclarationDTO>> list(
            @ApiParam("页码") @RequestParam(defaultValue = "1") Long current,
            @ApiParam("每页条数") @RequestParam(defaultValue = "10") Long size,
            @ApiParam("申报名称") @RequestParam(required = false) String declarationName,
            @ApiParam("申报周期") @RequestParam(required = false) String declarationPeriod,
            @ApiParam("状态") @RequestParam(required = false) String status) {
        Page<CbamDeclaration> page = new Page<>(current, size);
        LambdaQueryWrapper<CbamDeclaration> wrapper = new LambdaQueryWrapper<>();
        if (declarationName != null && !declarationName.isEmpty()) {
            wrapper.like(CbamDeclaration::getDeclarationName, declarationName);
        }
        if (declarationPeriod != null && !declarationPeriod.isEmpty()) {
            wrapper.eq(CbamDeclaration::getDeclarationPeriod, declarationPeriod);
        }
        if (status != null && !status.isEmpty()) {
            wrapper.eq(CbamDeclaration::getStatus, status);
        }
        wrapper.orderByDesc(CbamDeclaration::getCreateTime);
        Page<CbamDeclaration> resultPage = declarationService.page(page, wrapper);
        List<CbamDeclarationDTO> records = resultPage.getRecords().stream()
                .map(this::convertToDTO).collect(Collectors.toList());
        return Result.ok(PageResult.of(resultPage.getCurrent(), resultPage.getSize(), resultPage.getTotal(), records));
    }

    @ApiOperation("获取CBAM申报详情（含材料明细）")
    @GetMapping("/{id}")
    public Result<CbamDeclarationDTO> getById(@ApiParam("申报ID") @PathVariable Long id) {
        CbamDeclaration entity = declarationService.getById(id);
        if (entity == null) {
            return Result.error("申报记录不存在");
        }
        CbamDeclarationDTO dto = convertToDTO(entity);
        // 查询材料明细
        List<CbamDeclarationDetail> details = detailService.list(
                new LambdaQueryWrapper<CbamDeclarationDetail>()
                        .eq(CbamDeclarationDetail::getDeclarationId, id));
        dto.setDetails(details.stream().map(this::convertDetailToDTO).collect(Collectors.toList()));
        return Result.ok(dto);
    }

    @ApiOperation("新增CBAM申报")
    @PostMapping
    public Result<Void> add(@RequestBody CbamDeclarationDTO dto) {
        CbamDeclaration entity = new CbamDeclaration();
        BeanUtils.copyProperties(dto, entity);
        entity.setId(null);
        entity.setStatus("DRAFT");
        entity.setEmissionUnit("tCO2");
        declarationService.save(entity);

        // 保存材料明细
        if (dto.getDetails() != null && !dto.getDetails().isEmpty()) {
            for (CbamDeclarationDetailDTO detailDTO : dto.getDetails()) {
                CbamDeclarationDetail detail = new CbamDeclarationDetail();
                BeanUtils.copyProperties(detailDTO, detail);
                detail.setId(null);
                detail.setDeclarationId(entity.getId());
                // 自动计算排放量
                if (detail.getConsumption() != null && detail.getEmissionFactor() != null) {
                    detail.setEmission(detail.getConsumption().multiply(detail.getEmissionFactor()).setScale(4, RoundingMode.HALF_UP));
                }
                detailService.save(detail);
            }
        }

        return Result.ok();
    }

    @ApiOperation("修改CBAM申报")
    @PutMapping("/{id}")
    public Result<Void> update(@ApiParam("申报ID") @PathVariable Long id, @RequestBody CbamDeclarationDTO dto) {
        CbamDeclaration entity = new CbamDeclaration();
        BeanUtils.copyProperties(dto, entity);
        entity.setId(id);
        declarationService.updateById(entity);

        // 更新材料明细：先删除旧的，再插入新的
        if (dto.getDetails() != null) {
            detailService.remove(
                    new LambdaQueryWrapper<CbamDeclarationDetail>()
                            .eq(CbamDeclarationDetail::getDeclarationId, id));
            for (CbamDeclarationDetailDTO detailDTO : dto.getDetails()) {
                CbamDeclarationDetail detail = new CbamDeclarationDetail();
                BeanUtils.copyProperties(detailDTO, detail);
                detail.setId(null);
                detail.setDeclarationId(id);
                if (detail.getConsumption() != null && detail.getEmissionFactor() != null) {
                    detail.setEmission(detail.getConsumption().multiply(detail.getEmissionFactor()).setScale(4, RoundingMode.HALF_UP));
                }
                detailService.save(detail);
            }
        }

        return Result.ok();
    }

    @ApiOperation("删除CBAM申报")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@ApiParam("申报ID") @PathVariable Long id) {
        declarationService.removeById(id);
        // 同时删除明细
        detailService.remove(
                new LambdaQueryWrapper<CbamDeclarationDetail>()
                        .eq(CbamDeclarationDetail::getDeclarationId, id));
        return Result.ok();
    }

    @ApiOperation("排放计算（汇总直接+间接排放）")
    @PostMapping("/calculate/{id}")
    public Result<CbamDeclarationDTO> calculate(@ApiParam("申报ID") @PathVariable Long id) {
        CbamDeclaration declaration = declarationService.getById(id);
        if (declaration == null) {
            return Result.error("申报记录不存在");
        }

        // 查询所有材料明细
        List<CbamDeclarationDetail> details = detailService.list(
                new LambdaQueryWrapper<CbamDeclarationDetail>()
                        .eq(CbamDeclarationDetail::getDeclarationId, id));

        BigDecimal directEmission = BigDecimal.ZERO;
        BigDecimal indirectEmission = BigDecimal.ZERO;

        for (CbamDeclarationDetail detail : details) {
            // 重新计算排放量
            if (detail.getConsumption() != null && detail.getEmissionFactor() != null) {
                BigDecimal emission = detail.getConsumption().multiply(detail.getEmissionFactor()).setScale(4, RoundingMode.HALF_UP);
                detail.setEmission(emission);
                detailService.updateById(detail);

                // 根据材料类别区分直接/间接排放
                if ("DIRECT".equalsIgnoreCase(detail.getMaterialCategory())) {
                    directEmission = directEmission.add(emission);
                } else {
                    indirectEmission = indirectEmission.add(emission);
                }
            }
        }

        // 更新申报主表
        declaration.setDirectEmission(directEmission);
        declaration.setIndirectEmission(indirectEmission);
        declaration.setTotalEmission(directEmission.add(indirectEmission));
        declarationService.updateById(declaration);

        CbamDeclarationDTO dto = convertToDTO(declaration);
        dto.setDetails(details.stream().map(this::convertDetailToDTO).collect(Collectors.toList()));
        return Result.ok(dto);
    }

    @ApiOperation("提交审核")
    @PutMapping("/{id}/submit")
    public Result<Void> submit(@ApiParam("申报ID") @PathVariable Long id) {
        CbamDeclaration entity = declarationService.getById(id);
        if (entity == null) {
            return Result.error("申报记录不存在");
        }
        entity.setStatus("SUBMITTED");
        declarationService.updateById(entity);
        return Result.ok();
    }

    @ApiOperation("审核通过")
    @PutMapping("/{id}/approve")
    public Result<Void> approve(@ApiParam("申报ID") @PathVariable Long id) {
        CbamDeclaration entity = declarationService.getById(id);
        if (entity == null) {
            return Result.error("申报记录不存在");
        }
        entity.setStatus("APPROVED");
        declarationService.updateById(entity);
        return Result.ok();
    }

    @ApiOperation("审核驳回")
    @PutMapping("/{id}/reject")
    public Result<Void> reject(@ApiParam("申报ID") @PathVariable Long id) {
        CbamDeclaration entity = declarationService.getById(id);
        if (entity == null) {
            return Result.error("申报记录不存在");
        }
        entity.setStatus("REJECTED");
        declarationService.updateById(entity);
        return Result.ok();
    }

    private CbamDeclarationDTO convertToDTO(CbamDeclaration entity) {
        CbamDeclarationDTO dto = new CbamDeclarationDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }

    private CbamDeclarationDetailDTO convertDetailToDTO(CbamDeclarationDetail entity) {
        CbamDeclarationDetailDTO dto = new CbamDeclarationDetailDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }
}
