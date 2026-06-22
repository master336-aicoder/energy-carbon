package com.seatone.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.seatone.common.result.PageResult;
import com.seatone.common.result.Result;
import com.seatone.system.dto.AiServiceDTO;
import com.seatone.system.entity.AiServiceRecord;
import com.seatone.system.service.AiServiceRecordService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Api(tags = "AI服务预约管理")
@RestController
@RequestMapping("/ai/service")
public class AiServiceController {

    @Autowired
    private AiServiceRecordService serviceRecordService;

    @ApiOperation("分页查询服务预约列表")
    @GetMapping("/list")
    public Result<PageResult<AiServiceDTO>> list(
            @ApiParam("页码") @RequestParam(defaultValue = "1") Long current,
            @ApiParam("每页条数") @RequestParam(defaultValue = "10") Long size,
            @ApiParam("服务类型") @RequestParam(required = false) String serviceType,
            @ApiParam("状态") @RequestParam(required = false) String status,
            @ApiParam("申请人") @RequestParam(required = false) String applicant) {
        Page<AiServiceRecord> page = new Page<>(current, size);
        LambdaQueryWrapper<AiServiceRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AiServiceRecord::getDeleted, 0);
        if (serviceType != null && !serviceType.isEmpty()) {
            wrapper.eq(AiServiceRecord::getServiceType, serviceType);
        }
        if (status != null && !status.isEmpty()) {
            wrapper.eq(AiServiceRecord::getStatus, status);
        }
        if (applicant != null && !applicant.isEmpty()) {
            wrapper.like(AiServiceRecord::getApplicant, applicant);
        }
        wrapper.orderByDesc(AiServiceRecord::getCreateTime);
        Page<AiServiceRecord> resultPage = serviceRecordService.page(page, wrapper);
        List<AiServiceDTO> records = resultPage.getRecords().stream().map(this::convertToDTO).collect(Collectors.toList());
        return Result.ok(PageResult.of(resultPage.getCurrent(), resultPage.getSize(), resultPage.getTotal(), records));
    }

    @ApiOperation("新增服务预约")
    @PostMapping
    public Result<Void> add(@RequestBody AiServiceRecord record) {
        record.setStatus("PENDING");
        record.setDeleted(0);
        serviceRecordService.save(record);
        return Result.ok();
    }

    @ApiOperation("取消预约")
    @PutMapping("/{id}/cancel")
    public Result<Void> cancel(@ApiParam("记录ID") @PathVariable Long id) {
        AiServiceRecord record = new AiServiceRecord();
        record.setId(id);
        record.setStatus("REJECTED");
        record.setRemark("用户取消预约");
        serviceRecordService.updateById(record);
        return Result.ok();
    }

    @ApiOperation("获取预约详情")
    @GetMapping("/{id}")
    public Result<AiServiceDTO> getById(@ApiParam("记录ID") @PathVariable Long id) {
        AiServiceRecord entity = serviceRecordService.getById(id);
        if (entity == null) {
            return Result.error("预约记录不存在");
        }
        return Result.ok(convertToDTO(entity));
    }

    @ApiOperation("更新预约状态")
    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(@ApiParam("记录ID") @PathVariable Long id,
                                      @ApiParam("状态") @RequestParam String status,
                                      @ApiParam("结果") @RequestParam(required = false) String result) {
        AiServiceRecord record = new AiServiceRecord();
        record.setId(id);
        record.setStatus(status);
        record.setResult(result);
        serviceRecordService.updateById(record);
        return Result.ok();
    }

    @ApiOperation("删除预约记录")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@ApiParam("记录ID") @PathVariable Long id) {
        AiServiceRecord record = new AiServiceRecord();
        record.setId(id);
        record.setDeleted(1);
        serviceRecordService.updateById(record);
        return Result.ok();
    }

    private AiServiceDTO convertToDTO(AiServiceRecord entity) {
        AiServiceDTO dto = new AiServiceDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }
}
