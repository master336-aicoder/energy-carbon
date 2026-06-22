package com.seatone.monitor.controller;

import com.seatone.common.result.Result;
import com.seatone.monitor.dto.CompareDataDTO;
import com.seatone.monitor.entity.MonitorHistoryData;
import com.seatone.monitor.service.MonitorHistoryDataService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Api(tags = "设备对标")
@RestController
@RequestMapping("/monitor/device-compare")
public class DeviceCompareController {

    @Autowired
    private MonitorHistoryDataService historyDataService;

    @ApiOperation("双设备同时段曲线数据对比")
    @GetMapping("/compare")
    public Result<List<CompareDataDTO>> compare(
            @ApiParam("设备A ID") @RequestParam Long meterIdA,
            @ApiParam("设备B ID") @RequestParam Long meterIdB,
            @ApiParam("数据类型") @RequestParam(required = false) String dataType,
            @ApiParam("开始时间") @RequestParam String startTime,
            @ApiParam("结束时间") @RequestParam String endTime) {
        List<CompareDataDTO> result = new ArrayList<>();

        // 设备A数据
        List<MonitorHistoryData> dataA = historyDataService.lambdaQuery()
                .eq(MonitorHistoryData::getMeterId, meterIdA)
                .eq(dataType != null && !dataType.isEmpty(), MonitorHistoryData::getDataType, dataType)
                .ge(MonitorHistoryData::getDataTime, LocalDateTime.parse(startTime))
                .le(MonitorHistoryData::getDataTime, LocalDateTime.parse(endTime))
                .orderByAsc(MonitorHistoryData::getDataTime)
                .list();
        CompareDataDTO seriesA = new CompareDataDTO();
        seriesA.setSeriesName("设备A");
        seriesA.setColor("#409EFF");
        List<CompareDataDTO.DataPoint> pointsA = new ArrayList<>();
        for (MonitorHistoryData d : dataA) {
            CompareDataDTO.DataPoint p = new CompareDataDTO.DataPoint();
            p.setTime(d.getDataTime().toString());
            p.setValue(d.getDataValue() != null ? d.getDataValue().doubleValue() : 0);
            pointsA.add(p);
        }
        seriesA.setData(pointsA);
        result.add(seriesA);

        // 设备B数据
        List<MonitorHistoryData> dataB = historyDataService.lambdaQuery()
                .eq(MonitorHistoryData::getMeterId, meterIdB)
                .eq(dataType != null && !dataType.isEmpty(), MonitorHistoryData::getDataType, dataType)
                .ge(MonitorHistoryData::getDataTime, LocalDateTime.parse(startTime))
                .le(MonitorHistoryData::getDataTime, LocalDateTime.parse(endTime))
                .orderByAsc(MonitorHistoryData::getDataTime)
                .list();
        CompareDataDTO seriesB = new CompareDataDTO();
        seriesB.setSeriesName("设备B");
        seriesB.setColor("#E6A23C");
        List<CompareDataDTO.DataPoint> pointsB = new ArrayList<>();
        for (MonitorHistoryData d : dataB) {
            CompareDataDTO.DataPoint p = new CompareDataDTO.DataPoint();
            p.setTime(d.getDataTime().toString());
            p.setValue(d.getDataValue() != null ? d.getDataValue().doubleValue() : 0);
            pointsB.add(p);
        }
        seriesB.setData(pointsB);
        result.add(seriesB);

        return Result.ok(result);
    }
}