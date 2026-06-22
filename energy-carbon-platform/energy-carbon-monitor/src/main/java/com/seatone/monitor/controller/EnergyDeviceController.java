package com.seatone.monitor.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.seatone.common.result.PageResult;
import com.seatone.common.result.Result;
import com.seatone.monitor.dto.EnergyDeviceDTO;
import com.seatone.monitor.entity.EnergyDevice;
import com.seatone.monitor.service.EnergyDeviceService;
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

@Api(tags = "用能设备管理")
@RestController
@RequestMapping("/monitor/device")
public class EnergyDeviceController {

    @Autowired
    private EnergyDeviceService deviceService;

    @ApiOperation("分页查询用能设备列表")
    @GetMapping("/list")
    public Result<PageResult<EnergyDeviceDTO>> list(
            @ApiParam("页码") @RequestParam(defaultValue = "1") Long current,
            @ApiParam("每页条数") @RequestParam(defaultValue = "10") Long size,
            @ApiParam("设备名称") @RequestParam(required = false) String deviceName,
            @ApiParam("设备类型") @RequestParam(required = false) String deviceType,
            @ApiParam("状态") @RequestParam(required = false) Integer status) {
        Page<EnergyDevice> page = new Page<>(current, size);
        LambdaQueryWrapper<EnergyDevice> wrapper = new LambdaQueryWrapper<>();
        if (deviceName != null && !deviceName.isEmpty()) {
            wrapper.like(EnergyDevice::getDeviceName, deviceName);
        }
        if (deviceType != null && !deviceType.isEmpty()) {
            wrapper.eq(EnergyDevice::getDeviceType, deviceType);
        }
        if (status != null) {
            wrapper.eq(EnergyDevice::getStatus, status);
        }
        wrapper.orderByDesc(EnergyDevice::getCreateTime);
        Page<EnergyDevice> resultPage = deviceService.page(page, wrapper);
        List<EnergyDeviceDTO> records = resultPage.getRecords().stream().map(this::convertToDTO).collect(Collectors.toList());
        return Result.ok(PageResult.of(resultPage.getCurrent(), resultPage.getSize(), resultPage.getTotal(), records));
    }

    @ApiOperation("根据ID查询用能设备")
    @GetMapping("/{id}")
    public Result<EnergyDeviceDTO> getById(@ApiParam("设备ID") @PathVariable Long id) {
        EnergyDevice device = deviceService.getById(id);
        if (device == null) {
            return Result.error("设备不存在");
        }
        return Result.ok(convertToDTO(device));
    }

    @ApiOperation("新增用能设备")
    @PostMapping
    public Result<Void> add(@RequestBody EnergyDeviceDTO dto) {
        EnergyDevice device = new EnergyDevice();
        BeanUtils.copyProperties(dto, device);
        device.setId(null);
        deviceService.save(device);
        return Result.ok();
    }

    @ApiOperation("修改用能设备")
    @PutMapping("/{id}")
    public Result<Void> update(@ApiParam("设备ID") @PathVariable Long id, @RequestBody EnergyDeviceDTO dto) {
        EnergyDevice device = new EnergyDevice();
        BeanUtils.copyProperties(dto, device);
        device.setId(id);
        deviceService.updateById(device);
        return Result.ok();
    }

    @ApiOperation("删除用能设备")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@ApiParam("设备ID") @PathVariable Long id) {
        deviceService.removeById(id);
        return Result.ok();
    }

    @ApiOperation("修改用能设备状态")
    @PutMapping("/{id}/status")
    public Result<Void> changeStatus(@ApiParam("设备ID") @PathVariable Long id,
                                      @ApiParam("状态") @RequestParam Integer status) {
        EnergyDevice device = new EnergyDevice();
        device.setId(id);
        device.setStatus(status);
        deviceService.updateById(device);
        return Result.ok();
    }

    @ApiOperation("导出用能设备")
    @GetMapping("/export")
    public void export(HttpServletResponse response,
                       @ApiParam("设备名称") @RequestParam(required = false) String deviceName,
                       @ApiParam("设备类型") @RequestParam(required = false) String deviceType,
                       @ApiParam("状态") @RequestParam(required = false) Integer status) throws IOException {
        LambdaQueryWrapper<EnergyDevice> wrapper = new LambdaQueryWrapper<>();
        if (deviceName != null && !deviceName.isEmpty()) {
            wrapper.like(EnergyDevice::getDeviceName, deviceName);
        }
        if (deviceType != null && !deviceType.isEmpty()) {
            wrapper.eq(EnergyDevice::getDeviceType, deviceType);
        }
        if (status != null) {
            wrapper.eq(EnergyDevice::getStatus, status);
        }
        List<EnergyDevice> list = deviceService.list(wrapper);

        response.setContentType("text/csv;charset=UTF-8");
        response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode("用能设备台账.csv", StandardCharsets.UTF_8.name()));
        OutputStream out = response.getOutputStream();
        out.write(new byte[]{(byte) 0xEF, (byte) 0xBB, (byte) 0xBF});
        out.write("设备编码,设备名称,设备类型,制造商,型号,额定功率(kW),电压等级,安装日期,状态,备注\n".getBytes(StandardCharsets.UTF_8));
        for (EnergyDevice d : list) {
            String line = String.format("%s,%s,%s,%s,%s,%s,%s,%s,%s,%s\n",
                    d.getDeviceCode() == null ? "" : d.getDeviceCode(),
                    d.getDeviceName() == null ? "" : d.getDeviceName(),
                    d.getDeviceType() == null ? "" : d.getDeviceType(),
                    d.getManufacturer() == null ? "" : d.getManufacturer(),
                    d.getModel() == null ? "" : d.getModel(),
                    d.getPowerRating() == null ? "" : d.getPowerRating().toString(),
                    d.getVoltageLevel() == null ? "" : d.getVoltageLevel(),
                    d.getInstallDate() == null ? "" : d.getInstallDate().toString(),
                    d.getStatus() != null && d.getStatus() == 1 ? "正常" : "停用",
                    d.getRemark() == null ? "" : d.getRemark());
            out.write(line.getBytes(StandardCharsets.UTF_8));
        }
        out.flush();
    }

    @ApiOperation("导入用能设备")
    @PostMapping("/import")
    public Result<Void> importData(@RequestBody List<EnergyDeviceDTO> dtoList) {
        for (EnergyDeviceDTO dto : dtoList) {
            EnergyDevice device = new EnergyDevice();
            BeanUtils.copyProperties(dto, device);
            device.setId(null);
            deviceService.save(device);
        }
        return Result.ok();
    }

    private EnergyDeviceDTO convertToDTO(EnergyDevice device) {
        EnergyDeviceDTO dto = new EnergyDeviceDTO();
        BeanUtils.copyProperties(device, dto);
        return dto;
    }
}
