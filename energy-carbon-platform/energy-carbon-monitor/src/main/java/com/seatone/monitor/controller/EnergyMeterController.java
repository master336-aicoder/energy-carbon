package com.seatone.monitor.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.seatone.common.result.PageResult;
import com.seatone.common.result.Result;
import com.seatone.monitor.dto.EnergyMeterDTO;
import com.seatone.monitor.entity.EnergyMeter;
import com.seatone.monitor.service.EnergyMeterService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

@Api(tags = "计量器具管理")
@RestController
@RequestMapping("/monitor/meter")
public class EnergyMeterController {

    @Autowired
    private EnergyMeterService meterService;

    @ApiOperation("分页查询计量器具列表")
    @GetMapping("/list")
    public Result<PageResult<EnergyMeterDTO>> list(
            @ApiParam("页码") @RequestParam(defaultValue = "1") Long current,
            @ApiParam("每页条数") @RequestParam(defaultValue = "10") Long size,
            @ApiParam("计量器具名称") @RequestParam(required = false) String meterName,
            @ApiParam("计量器具类型") @RequestParam(required = false) String meterType,
            @ApiParam("状态") @RequestParam(required = false) Integer status) {
        Page<EnergyMeter> page = new Page<>(current, size);
        LambdaQueryWrapper<EnergyMeter> wrapper = new LambdaQueryWrapper<>();
        if (meterName != null && !meterName.isEmpty()) {
            wrapper.like(EnergyMeter::getMeterName, meterName);
        }
        if (meterType != null && !meterType.isEmpty()) {
            wrapper.eq(EnergyMeter::getMeterType, meterType);
        }
        if (status != null) {
            wrapper.eq(EnergyMeter::getStatus, status);
        }
        wrapper.orderByDesc(EnergyMeter::getCreateTime);
        Page<EnergyMeter> resultPage = meterService.page(page, wrapper);
        List<EnergyMeterDTO> records = resultPage.getRecords().stream().map(this::convertToDTO).collect(Collectors.toList());
        return Result.ok(PageResult.of(resultPage.getCurrent(), resultPage.getSize(), resultPage.getTotal(), records));
    }

    @ApiOperation("根据ID查询计量器具")
    @GetMapping("/{id}")
    public Result<EnergyMeterDTO> getById(@ApiParam("计量器具ID") @PathVariable Long id) {
        EnergyMeter meter = meterService.getById(id);
        if (meter == null) {
            return Result.error("计量器具不存在");
        }
        return Result.ok(convertToDTO(meter));
    }

    @ApiOperation("新增计量器具")
    @PostMapping
    public Result<Void> add(@RequestBody EnergyMeterDTO dto) {
        EnergyMeter meter = new EnergyMeter();
        BeanUtils.copyProperties(dto, meter);
        meter.setId(null);
        meterService.save(meter);
        return Result.ok();
    }

    @ApiOperation("修改计量器具")
    @PutMapping("/{id}")
    public Result<Void> update(@ApiParam("计量器具ID") @PathVariable Long id, @RequestBody EnergyMeterDTO dto) {
        EnergyMeter meter = new EnergyMeter();
        BeanUtils.copyProperties(dto, meter);
        meter.setId(id);
        meterService.updateById(meter);
        return Result.ok();
    }

    @ApiOperation("删除计量器具")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@ApiParam("计量器具ID") @PathVariable Long id) {
        meterService.removeById(id);
        return Result.ok();
    }

    @ApiOperation("修改计量器具状态")
    @PutMapping("/{id}/status")
    public Result<Void> changeStatus(@ApiParam("计量器具ID") @PathVariable Long id,
                                      @ApiParam("状态") @RequestParam Integer status) {
        EnergyMeter meter = new EnergyMeter();
        meter.setId(id);
        meter.setStatus(status);
        meterService.updateById(meter);
        return Result.ok();
    }

    @ApiOperation("导出计量器具")
    @GetMapping("/export")
    public void export(HttpServletResponse response,
                       @ApiParam("计量器具名称") @RequestParam(required = false) String meterName,
                       @ApiParam("计量器具类型") @RequestParam(required = false) String meterType,
                       @ApiParam("状态") @RequestParam(required = false) Integer status) throws IOException {
        LambdaQueryWrapper<EnergyMeter> wrapper = new LambdaQueryWrapper<>();
        if (meterName != null && !meterName.isEmpty()) {
            wrapper.like(EnergyMeter::getMeterName, meterName);
        }
        if (meterType != null && !meterType.isEmpty()) {
            wrapper.eq(EnergyMeter::getMeterType, meterType);
        }
        if (status != null) {
            wrapper.eq(EnergyMeter::getStatus, status);
        }
        List<EnergyMeter> list = meterService.list(wrapper);

        response.setContentType("text/csv;charset=UTF-8");
        response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode("计量器具台账.csv", StandardCharsets.UTF_8.name()));
        OutputStream out = response.getOutputStream();
        out.write(new byte[]{(byte) 0xEF, (byte) 0xBB, (byte) 0xBF});
        out.write("计量器具编码,计量器具名称,计量器具类型,准确度等级,量程下限,量程上限,安装日期,检定日期,下次检定日期,状态,备注\n".getBytes(StandardCharsets.UTF_8));
        for (EnergyMeter m : list) {
            String line = String.format("%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s\n",
                    m.getMeterCode() == null ? "" : m.getMeterCode(),
                    m.getMeterName() == null ? "" : m.getMeterName(),
                    m.getMeterType() == null ? "" : m.getMeterType(),
                    m.getAccuracyClass() == null ? "" : m.getAccuracyClass(),
                    m.getRangeMin() == null ? "" : m.getRangeMin().toString(),
                    m.getRangeMax() == null ? "" : m.getRangeMax().toString(),
                    m.getInstallDate() == null ? "" : m.getInstallDate().toString(),
                    m.getCalibrationDate() == null ? "" : m.getCalibrationDate().toString(),
                    m.getNextCalibrationDate() == null ? "" : m.getNextCalibrationDate().toString(),
                    m.getStatus() != null && m.getStatus() == 1 ? "正常" : "停用",
                    m.getRemark() == null ? "" : m.getRemark());
            out.write(line.getBytes(StandardCharsets.UTF_8));
        }
        out.flush();
    }

    @ApiOperation("导入计量器具")
    @PostMapping("/import")
    public Result<Void> importData(@RequestBody List<EnergyMeterDTO> dtoList) {
        for (EnergyMeterDTO dto : dtoList) {
            EnergyMeter meter = new EnergyMeter();
            BeanUtils.copyProperties(dto, meter);
            meter.setId(null);
            meterService.save(meter);
        }
        return Result.ok();
    }

    private EnergyMeterDTO convertToDTO(EnergyMeter meter) {
        EnergyMeterDTO dto = new EnergyMeterDTO();
        BeanUtils.copyProperties(meter, dto);
        return dto;
    }
}
