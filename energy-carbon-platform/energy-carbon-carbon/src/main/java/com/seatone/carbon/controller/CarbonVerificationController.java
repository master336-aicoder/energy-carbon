package com.seatone.carbon.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.seatone.carbon.dto.CarbonVerificationDTO;
import com.seatone.carbon.entity.CarbonVerification;
import com.seatone.carbon.service.CarbonVerificationService;
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

@Api(tags = "碳核查")
@RestController
@RequestMapping("/carbon/verification")
public class CarbonVerificationController {

    @Autowired
    private CarbonVerificationService verificationService;

    @ApiOperation("分页查询碳核查列表")
    @GetMapping("/list")
    public Result<PageResult<CarbonVerificationDTO>> list(
            @ApiParam("页码") @RequestParam(defaultValue = "1") Long current,
            @ApiParam("每页条数") @RequestParam(defaultValue = "10") Long size,
            @ApiParam("核查名称") @RequestParam(required = false) String verificationName,
            @ApiParam("关联核算ID") @RequestParam(required = false) Long accountingId,
            @ApiParam("状态") @RequestParam(required = false) String status) {
        Page<CarbonVerification> page = new Page<>(current, size);
        LambdaQueryWrapper<CarbonVerification> wrapper = new LambdaQueryWrapper<>();
        if (verificationName != null && !verificationName.isEmpty()) {
            wrapper.like(CarbonVerification::getVerificationName, verificationName);
        }
        if (accountingId != null) {
            wrapper.eq(CarbonVerification::getAccountingId, accountingId);
        }
        if (status != null && !status.isEmpty()) {
            wrapper.eq(CarbonVerification::getStatus, status);
        }
        wrapper.orderByDesc(CarbonVerification::getCreateTime);
        Page<CarbonVerification> resultPage = verificationService.page(page, wrapper);
        List<CarbonVerificationDTO> records = resultPage.getRecords().stream()
                .map(this::convertToDTO).collect(Collectors.toList());
        return Result.ok(PageResult.of(resultPage.getCurrent(), resultPage.getSize(), resultPage.getTotal(), records));
    }

    @ApiOperation("获取碳核查详情")
    @GetMapping("/{id}")
    public Result<CarbonVerificationDTO> getById(@ApiParam("核查ID") @PathVariable Long id) {
        CarbonVerification entity = verificationService.getById(id);
        if (entity == null) {
            return Result.error("核查记录不存在");
        }
        return Result.ok(convertToDTO(entity));
    }

    @ApiOperation("新增碳核查")
    @PostMapping
    public Result<Void> add(@RequestBody @Valid CarbonVerificationDTO dto) {
        CarbonVerification entity = new CarbonVerification();
        BeanUtils.copyProperties(dto, entity);
        entity.setId(null);
        entity.setStatus("PENDING");
        verificationService.save(entity);
        return Result.ok();
    }

    @ApiOperation("修改碳核查")
    @PutMapping("/{id}")
    public Result<Void> update(@ApiParam("核查ID") @PathVariable Long id, @RequestBody CarbonVerificationDTO dto) {
        CarbonVerification entity = new CarbonVerification();
        BeanUtils.copyProperties(dto, entity);
        entity.setId(id);
        verificationService.updateById(entity);
        return Result.ok();
    }

    @ApiOperation("删除碳核查")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@ApiParam("核查ID") @PathVariable Long id) {
        verificationService.removeById(id);
        return Result.ok();
    }

    @ApiOperation("提交核查结果")
    @PutMapping("/{id}/complete")
    public Result<Void> complete(@ApiParam("核查ID") @PathVariable Long id,
                                  @RequestParam String verificationResult) {
        CarbonVerification entity = verificationService.getById(id);
        if (entity == null) {
            return Result.error("核查记录不存在");
        }
        entity.setVerificationResult(verificationResult);
        entity.setStatus("COMPLETED");
        verificationService.updateById(entity);
        return Result.ok();
    }

    @ApiOperation("按核算查询核查记录")
    @GetMapping("/byAccounting/{accountingId}")
    public Result<List<CarbonVerificationDTO>> getByAccounting(
            @ApiParam("核算ID") @PathVariable Long accountingId) {
        List<CarbonVerification> list = verificationService.list(
                new LambdaQueryWrapper<CarbonVerification>()
                        .eq(CarbonVerification::getAccountingId, accountingId));
        return Result.ok(list.stream().map(this::convertToDTO).collect(Collectors.toList()));
    }

    private CarbonVerificationDTO convertToDTO(CarbonVerification entity) {
        CarbonVerificationDTO dto = new CarbonVerificationDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }
}