package com.seatone.monitor.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.seatone.common.result.PageResult;
import com.seatone.common.result.Result;
import com.seatone.monitor.dto.WorkShiftDTO;
import com.seatone.monitor.entity.WorkShift;
import com.seatone.monitor.service.WorkShiftService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Api(tags = "班组管理")
@RestController
@RequestMapping("/monitor/workShift")
public class WorkShiftController {

    @Autowired
    private WorkShiftService workShiftService;

    @ApiOperation("分页查询班组列表")
    @GetMapping("/list")
    public Result<PageResult<WorkShiftDTO>> list(
            @ApiParam("页码") @RequestParam(defaultValue = "1") Long current,
            @ApiParam("每页条数") @RequestParam(defaultValue = "10") Long size,
            @ApiParam("班组名称") @RequestParam(required = false) String shiftName,
            @ApiParam("状态") @RequestParam(required = false) Integer status) {
        Page<WorkShift> page = new Page<>(current, size);
        LambdaQueryWrapper<WorkShift> wrapper = new LambdaQueryWrapper<>();
        if (shiftName != null && !shiftName.isEmpty()) {
            wrapper.like(WorkShift::getShiftName, shiftName);
        }
        if (status != null) {
            wrapper.eq(WorkShift::getStatus, status);
        }
        wrapper.orderByDesc(WorkShift::getCreateTime);
        Page<WorkShift> resultPage = workShiftService.page(page, wrapper);
        List<WorkShiftDTO> records = resultPage.getRecords().stream().map(this::convertToDTO).collect(Collectors.toList());
        return Result.ok(PageResult.of(resultPage.getCurrent(), resultPage.getSize(), resultPage.getTotal(), records));
    }

    @ApiOperation("查询所有班组")
    @GetMapping("/all")
    public Result<List<WorkShiftDTO>> all() {
        LambdaQueryWrapper<WorkShift> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(WorkShift::getStatus, 1);
        List<WorkShiftDTO> list = workShiftService.list(wrapper).stream().map(this::convertToDTO).collect(Collectors.toList());
        return Result.ok(list);
    }

    @ApiOperation("根据ID查询班组")
    @GetMapping("/{id}")
    public Result<WorkShiftDTO> getById(@ApiParam("班组ID") @PathVariable Long id) {
        WorkShift shift = workShiftService.getById(id);
        if (shift == null) {
            return Result.error("班组不存在");
        }
        return Result.ok(convertToDTO(shift));
    }

    @ApiOperation("新增班组")
    @PostMapping
    public Result<Void> add(@RequestBody WorkShiftDTO dto) {
        WorkShift shift = new WorkShift();
        BeanUtils.copyProperties(dto, shift);
        shift.setId(null);
        workShiftService.save(shift);
        return Result.ok();
    }

    @ApiOperation("修改班组")
    @PutMapping("/{id}")
    public Result<Void> update(@ApiParam("班组ID") @PathVariable Long id, @RequestBody WorkShiftDTO dto) {
        WorkShift shift = new WorkShift();
        BeanUtils.copyProperties(dto, shift);
        shift.setId(id);
        workShiftService.updateById(shift);
        return Result.ok();
    }

    @ApiOperation("删除班组")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@ApiParam("班组ID") @PathVariable Long id) {
        workShiftService.removeById(id);
        return Result.ok();
    }

    @ApiOperation("修改班组状态")
    @PutMapping("/{id}/status")
    public Result<Void> changeStatus(@ApiParam("班组ID") @PathVariable Long id,
                                      @ApiParam("状态") @RequestParam Integer status) {
        WorkShift shift = new WorkShift();
        shift.setId(id);
        shift.setStatus(status);
        workShiftService.updateById(shift);
        return Result.ok();
    }

    private WorkShiftDTO convertToDTO(WorkShift shift) {
        WorkShiftDTO dto = new WorkShiftDTO();
        BeanUtils.copyProperties(shift, dto);
        return dto;
    }
}
